package lk.jiat.global.service;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import lk.jiat.global.entity.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@DisplayName("Supply Chain Timer Service Integration Tests")
class SupplyChainTimerServiceArquillianTest {

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
    private SupplyChainTimerService timerService;

    @EJB
    private InventoryServiceLocal inventoryService;

    @EJB
    private ShipmentServiceLocal shipmentService;

    @EJB
    private VendorServiceLocal vendorService;

    @EJB
    private WarehouseServiceLocal warehouseService;

    @EJB
    private CarrierServiceLocal carrierService;

    @EJB
    private AlertServiceLocal alertService;

    @EJB
    private SystemTimerServiceLocal systemTimerService;

    @EJB
    private CustomsServiceLocal customsService;

    @EJB
    private UserServiceLocal userService;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    private Warehouse warehouse;
    private Vendor vendor;
    private Carrier carrier;
    private InventoryItem item;
    private User createdBy;

    // Use unique names to avoid conflicts
    private static final String TIMER_USERNAME = "timeruser_" + System.currentTimeMillis();

    @BeforeEach
    void setUp() throws Exception {
        // Check if there's an active transaction and rollback if needed
        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
            userTransaction.rollback();
        }

        userTransaction.begin();
        try {
            // ===== CLEAN UP IN CORRECT ORDER (child tables first) =====
            em.createQuery("DELETE FROM Alert").executeUpdate();
            em.createQuery("DELETE FROM SystemTimer").executeUpdate();
            em.createQuery("DELETE FROM CustomsDocument").executeUpdate();
            em.createQuery("DELETE FROM ShipmentStatusHistory").executeUpdate();
            em.createQuery("DELETE FROM ShipmentOrder").executeUpdate();
            em.createQuery("DELETE FROM ShipmentItem").executeUpdate();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createQuery("DELETE FROM InventoryItem").executeUpdate();
            em.createQuery("DELETE FROM VendorPerformanceEvaluation").executeUpdate();
            em.createQuery("DELETE FROM Vendor").executeUpdate();
            em.createQuery("DELETE FROM Carrier").executeUpdate();
            em.createQuery("DELETE FROM Warehouse").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.flush();

            // ===== CREATE ROLES =====
            Role role = new Role("LOGISTICS_COORDINATOR", "LC Role");
            em.persist(role);
            em.flush();

            // ===== CREATE USER WITH UNIQUE USERNAME =====
            createdBy = new User(TIMER_USERNAME, "hash", "timer@test.com", "Timer User", role);
            createdBy.setStatus("ACTIVE");
            em.persist(createdBy);
            em.flush();

            // ===== CREATE TEST DATA =====
            warehouse = warehouseService.createWarehouse("Timer WH", "Location", "Sri Lanka", 10000);
            vendor = vendorService.createVendor("Timer Vendor", "timer@vendor.com", "123", "Sri Lanka", "ACTIVE");
            carrier = carrierService.createCarrier("Timer Carrier", "Standard", "info@timer.com");

            item = inventoryService.createItem(
                    "TIMER-SKU-001",
                    "Timer Test Item",
                    warehouse.getWarehouseId(),
                    100,
                    10,
                    BigDecimal.valueOf(100.0)
            );

            userTransaction.commit();

        } catch (Exception e) {
            userTransaction.rollback();
            throw e;
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
            userTransaction.rollback();
        }

        userTransaction.begin();
        try {
            em.createQuery("DELETE FROM Alert").executeUpdate();
            em.createQuery("DELETE FROM SystemTimer").executeUpdate();
            em.createQuery("DELETE FROM CustomsDocument").executeUpdate();
            em.createQuery("DELETE FROM ShipmentStatusHistory").executeUpdate();
            em.createQuery("DELETE FROM ShipmentOrder").executeUpdate();
            em.createQuery("DELETE FROM ShipmentItem").executeUpdate();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createQuery("DELETE FROM InventoryItem").executeUpdate();
            em.createQuery("DELETE FROM VendorPerformanceEvaluation").executeUpdate();
            em.createQuery("DELETE FROM Vendor").executeUpdate();
            em.createQuery("DELETE FROM Carrier").executeUpdate();
            em.createQuery("DELETE FROM Warehouse").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            userTransaction.commit();
        } catch (Exception e) {
            userTransaction.rollback();
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    // ============================================================
    // 1. INVENTORY REORDER CHECK TESTS
    // ============================================================

    @Test
    @DisplayName("Inventory check should create alert for low stock")
    void checkInventoryReorderLevels_ShouldCreateAlertForLowStock() throws Exception {
        item.setQuantity(5);
        inventoryService.updateItem(
                item.getItemId(),
                item.getSku(),
                item.getName(),
                item.getWarehouse().getWarehouseId(),
                item.getQuantity(),
                item.getReorderLevel(),
                item.getUnitPrice()
        );

        timerService.checkInventoryReorderLevels();

        List<Alert> inventoryAlerts = alertService.getAlertsByType("LOW_INVENTORY");
        assertFalse(inventoryAlerts.isEmpty(), "Should have at least one LOW_INVENTORY alert");

        Optional<Alert> alertForItem = inventoryAlerts.stream()
                .filter(a -> "InventoryItem".equals(a.getEntityType()) &&
                        item.getItemId().equals(a.getEntityId()))
                .findFirst();

        assertTrue(alertForItem.isPresent(), "Should have alert for our item");
        Alert alert = alertForItem.get();
        assertEquals("LOW_INVENTORY", alert.getAlertType());
        assertTrue(alert.getMessage().contains(item.getName()), "Alert should contain item name");

        Optional<SystemTimer> timer = systemTimerService.getTimerByName("Inventory Reorder Check");
        assertTrue(timer.isPresent());
        assertEquals("ACTIVE", timer.get().getStatus());
        assertNotNull(timer.get().getLastRun());
    }

    @Test
    @DisplayName("Inventory check should not create alert for healthy stock")
    void checkInventoryReorderLevels_ShouldNotCreateAlertForHealthyStock() throws Exception {

        userTransaction.begin();
        em.createQuery("DELETE FROM Alert").executeUpdate();
        userTransaction.commit();

        // Run timer method
        timerService.checkInventoryReorderLevels();

        List<Alert> inventoryAlerts = alertService.getAlertsByType("LOW_INVENTORY");

        boolean alertForItem = inventoryAlerts.stream()
                .anyMatch(a -> "InventoryItem".equals(a.getEntityType()) &&
                        item.getItemId().equals(a.getEntityId()));

        assertFalse(alertForItem, "Should not create alert for healthy stock item");
    }

    // ============================================================
    // 2. SHIPMENT DELAY CHECK TESTS
    // ============================================================

    @Test
    @DisplayName("Shipment delay check should create alert for delayed shipments")
    void checkShipmentDelays_ShouldCreateAlertForDelayed() throws Exception {
        Shipment shipment = shipmentService.createShipment(
                "DELAY-SHIP-" + System.currentTimeMillis(),
                warehouse.getWarehouseId(),
                "Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "DELAYED",
                LocalDateTime.now().minusDays(5),
                null,
                createdBy.getUserId()
        );

        timerService.checkShipmentDelays();

        List<Alert> delayAlerts = alertService.getAlertsByType("SHIPMENT_DELAY");
        assertFalse(delayAlerts.isEmpty(), "Should have at least one SHIPMENT_DELAY alert");

        Optional<Alert> alertForShipment = delayAlerts.stream()
                .filter(a -> "Shipment".equals(a.getEntityType()) &&
                        shipment.getShipmentId().equals(a.getEntityId()))
                .findFirst();

        assertTrue(alertForShipment.isPresent(), "Should have alert for our shipment");
        Alert alert = alertForShipment.get();
        assertEquals("SHIPMENT_DELAY", alert.getAlertType());
        assertTrue(alert.getMessage().contains(shipment.getTrackingNumber()), "Alert should contain tracking number");

        Optional<SystemTimer> timer = systemTimerService.getTimerByName("Shipment Delay Check");
        assertTrue(timer.isPresent());
        assertEquals("ACTIVE", timer.get().getStatus());
        assertNotNull(timer.get().getLastRun());
    }

    // ============================================================
    // 3. CUSTOMS DEADLINE CHECK TESTS
    // ============================================================

    @Test
    @DisplayName("Customs deadline check should create alert for pending documents")
    void checkCustomsDeadlines_ShouldCreateAlertForPendingDocs() throws Exception {
        Shipment shipment = shipmentService.createShipment(
                "CUSTOMS-DOC-" + System.currentTimeMillis(),
                warehouse.getWarehouseId(),
                "Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        CustomsDocument doc = customsService.createDocument(
                shipment.getShipmentId(),
                "Import Declaration",
                createdBy.getUserId()
        );

        CustomsDocument submitted = customsService.submitDocument(
                doc.getDocId(),
                createdBy.getUserId()
        );

        userTransaction.begin();
        try {
            em.createQuery("UPDATE CustomsDocument c SET c.submittedDate = :oldDate WHERE c.docId = :docId")
                    .setParameter("oldDate", LocalDateTime.now().minusHours(13))
                    .setParameter("docId", doc.getDocId())
                    .executeUpdate();
            userTransaction.commit();
        } catch (Exception e) {
            userTransaction.rollback();
            throw e;
        }

        timerService.checkCustomsDeadlines();

        List<Alert> customsAlerts = alertService.getAlertsByType("CUSTOMS_DEADLINE");

        assertFalse(customsAlerts.isEmpty(), "Should have at least one CUSTOMS_DEADLINE alert");

        Optional<Alert> alertForDoc = customsAlerts.stream()
                .filter(a -> "CustomsDocument".equals(a.getEntityType()) &&
                        doc.getDocId().equals(a.getEntityId()))
                .findFirst();

        assertTrue(alertForDoc.isPresent(), "Should have alert for our customs document");
        Alert alert = alertForDoc.get();
        assertEquals("CUSTOMS_DEADLINE", alert.getAlertType());
        assertTrue(alert.getMessage().contains(doc.getDocId().toString()), "Alert should contain document ID");
    }

    // ============================================================
    // 4. SYSTEM TIMER RECORDING TESTS
    // ============================================================

    @Test
    @DisplayName("System timers should be recorded correctly")
    void systemTimers_ShouldBeRecorded() throws Exception {
        timerService.checkInventoryReorderLevels();
        timerService.checkShipmentDelays();
        timerService.checkCustomsDeadlines();

        List<SystemTimer> timers = systemTimerService.getAllTimers();

        assertTrue(timers.size() >= 3, "Should have at least 3 timers");

        List<String> timerNames = timers.stream()
                .map(SystemTimer::getTimerName)
                .toList();

        assertTrue(timerNames.contains("Inventory Reorder Check"));
        assertTrue(timerNames.contains("Shipment Delay Check"));
        assertTrue(timerNames.contains("Customs Deadline Check"));
    }

    // ============================================================
    // 5. PROGRAMMATIC TIMER TEST
    // ============================================================

    @Test
    @DisplayName("Programmatic timer should be created")
    void programmaticTimer_ShouldBeCreated() throws Exception {
        timerService.createProgrammaticTimer();

        Optional<SystemTimer> timer = systemTimerService.getTimerByName("Programmatic Hourly Timer");
        assertTrue(timer.isPresent());
        assertEquals("PROGRAMMATIC", timer.get().getTimerType());
        assertEquals("ACTIVE", timer.get().getStatus());
    }

    // ============================================================
    // 6. ALERT RESOLUTION TEST
    // ============================================================

    @Test
    @DisplayName("Resolved alerts should be tracked")
    void resolveAlert_ShouldMarkResolved() throws Exception {
        Alert alert = alertService.createAlert(
                "TEST_ALERT",
                "HIGH",
                "Test alert for resolution"
        );

        Alert resolved = alertService.resolveAlert(alert.getAlertId());

        assertTrue(resolved.getResolved());

        List<Alert> activeAlerts = alertService.getActiveAlerts();
        boolean found = activeAlerts.stream()
                .anyMatch(a -> a.getAlertId().equals(alert.getAlertId()));
        assertFalse(found);
    }
}