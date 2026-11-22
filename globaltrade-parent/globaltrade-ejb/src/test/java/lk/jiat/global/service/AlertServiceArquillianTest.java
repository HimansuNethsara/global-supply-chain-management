package lk.jiat.global.service;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

import lk.jiat.global.entity.Alert;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@DisplayName("Alert Service Integration Tests")
class AlertServiceArquillianTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "lk.jiat.global")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("org.hibernate.orm:hibernate-core")
                                .withTransitivity().asFile()
                )
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("com.mysql:mysql-connector-j")
                                .withTransitivity().asFile()
                );
    }

    @EJB
    private AlertServiceLocal alertService;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    @BeforeEach
    void setUp() throws Exception {
        userTransaction.begin();
        em.createQuery("DELETE FROM Alert").executeUpdate();
        userTransaction.commit();
    }

    @AfterEach
    void tearDown() throws Exception {
        userTransaction.begin();
        em.createQuery("DELETE FROM Alert").executeUpdate();
        userTransaction.commit();
    }

    @Test
    @DisplayName("Should create alert successfully")
    void createAlert_ShouldSucceed() throws ValidationException {
        Alert alert = alertService.createAlert(
                "TEST_ALERT",
                "HIGH",
                "Test alert message"
        );

        assertNotNull(alert);
        assertNotNull(alert.getAlertId());
        assertEquals("TEST_ALERT", alert.getAlertType());
        assertEquals("HIGH", alert.getSeverity());
        assertEquals("Test alert message", alert.getMessage());
        assertFalse(alert.getResolved());
        assertNotNull(alert.getCreatedAt());
    }

    @Test
    @DisplayName("Should create alert with entity reference")
    void createAlert_ShouldCreateWithEntityReference() throws ValidationException {
        Alert alert = alertService.createAlert(
                "SHIPMENT_DELAY",
                "Shipment",
                123,
                "MEDIUM",
                "Shipment delayed"
        );

        assertNotNull(alert);
        assertEquals("SHIPMENT_DELAY", alert.getAlertType());
        assertEquals("Shipment", alert.getEntityType());
        assertEquals(123, alert.getEntityId());
        assertEquals("MEDIUM", alert.getSeverity());
    }

    @Test
    @DisplayName("Should update existing alert on createOrUpdate")
    void createOrUpdateAlert_ShouldUpdateExisting() throws ValidationException {
        Alert initial = alertService.createAlert(
                "LOW_INVENTORY",
                "InventoryItem",
                100,
                "HIGH",
                "Low stock: 5 units remaining"
        );

        Alert updated = alertService.createOrUpdateAlert(
                "LOW_INVENTORY",
                "InventoryItem",
                100,
                "CRITICAL",
                "Low stock: 2 units remaining - URGENT!"
        );

        assertEquals(initial.getAlertId(), updated.getAlertId());
        assertEquals("CRITICAL", updated.getSeverity());
        assertEquals("Low stock: 2 units remaining - URGENT!", updated.getMessage());
        assertFalse(updated.getResolved());
    }

    @Test
    @DisplayName("Should get active alerts")
    void getActiveAlerts_ShouldReturnOnlyUnresolved() throws ValidationException, ResourceNotFoundException {
        alertService.createAlert("ALERT_1", "LOW", "Active alert");
        alertService.createAlert("ALERT_2", "LOW", "Another active alert");

        Alert resolved = alertService.createAlert("ALERT_3", "LOW", "Resolved alert");
        alertService.resolveAlert(resolved.getAlertId());

        List<Alert> activeAlerts = alertService.getActiveAlerts();

        assertEquals(2, activeAlerts.size());
        assertTrue(activeAlerts.stream().anyMatch(a -> "Active alert".equals(a.getMessage())));
        assertTrue(activeAlerts.stream().anyMatch(a -> "Another active alert".equals(a.getMessage())));
        assertFalse(activeAlerts.stream().anyMatch(a -> "Resolved alert".equals(a.getMessage())));
    }

    @Test
    @DisplayName("Should get alerts by type")
    void getAlertsByType_ShouldReturnFilteredAlerts() throws ValidationException {
        alertService.createAlert("SHIPMENT_DELAY", "HIGH", "Delay 1");
        alertService.createAlert("SHIPMENT_DELAY", "HIGH", "Delay 2");
        alertService.createAlert("LOW_INVENTORY", "MEDIUM", "Low stock");
        alertService.createAlert("VENDOR_ISSUE", "LOW", "Vendor issue");

        List<Alert> delayAlerts = alertService.getAlertsByType("SHIPMENT_DELAY");
        List<Alert> inventoryAlerts = alertService.getAlertsByType("LOW_INVENTORY");

        assertEquals(2, delayAlerts.size());
        assertEquals(1, inventoryAlerts.size());
    }

    @Test
    @DisplayName("Should resolve alert")
    void resolveAlert_ShouldMarkResolved() throws Exception {
        Alert alert = alertService.createAlert("TEST", "LOW", "Test alert");

        Alert resolved = alertService.resolveAlert(alert.getAlertId());

        assertTrue(resolved.getResolved());
        assertNotNull(resolved.getCreatedAt());

        List<Alert> activeAlerts = alertService.getActiveAlerts();
        assertFalse(activeAlerts.stream().anyMatch(a -> a.getAlertId().equals(alert.getAlertId())));
    }

    @Test
    @DisplayName("Should resolve all alerts")
    void resolveAllAlerts_ShouldResolveAll() throws ValidationException {
        alertService.createAlert("A1", "LOW", "Alert 1");
        alertService.createAlert("A2", "LOW", "Alert 2");
        alertService.createAlert("A3", "LOW", "Alert 3");

        int resolvedCount = alertService.resolveAllAlerts();

        assertEquals(3, resolvedCount);
        List<Alert> activeAlerts = alertService.getActiveAlerts();
        assertEquals(0, activeAlerts.size());
    }

    @Test
    @DisplayName("Should delete alert")
    void deleteAlert_ShouldRemoveAlert() throws Exception {
        Alert alert = alertService.createAlert("TEST", "LOW", "Delete test");

        alertService.deleteAlert(alert.getAlertId());

        List<Alert> allAlerts = alertService.getActiveAlerts();
        assertFalse(allAlerts.stream().anyMatch(a -> a.getAlertId().equals(alert.getAlertId())));
    }

    @Test
    @DisplayName("Should get alert count by severity")
    void getAlertCountBySeverity_ShouldReturnCorrectCount() throws ValidationException {
        alertService.createAlert("A1", "CRITICAL", "Critical alert");
        alertService.createAlert("A2", "CRITICAL", "Another critical");
        alertService.createAlert("A3", "HIGH", "High alert");
        alertService.createAlert("A4", "MEDIUM", "Medium alert");

        long criticalCount = alertService.getAlertCountBySeverity("CRITICAL");
        long highCount = alertService.getAlertCountBySeverity("HIGH");
        long mediumCount = alertService.getAlertCountBySeverity("MEDIUM");

        assertEquals(2, criticalCount);
        assertEquals(1, highCount);
        assertEquals(1, mediumCount);
    }

    // ========== EXCEPTION TEST CASES  ===========

    @Test
    @DisplayName("Should throw ResourceNotFoundException when resolving non-existent alert")
    void resolveAlert_WithNonExistentId_ShouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> alertService.resolveAlert(99999));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent alert")
    void deleteAlert_WithNonExistentId_ShouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> alertService.deleteAlert(99999));
    }

    @Test
    @DisplayName("Should throw ValidationException when creating alert with null type")
    void createAlert_WithNullType_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> alertService.createAlert(null, "LOW", "Test message"));
    }
}