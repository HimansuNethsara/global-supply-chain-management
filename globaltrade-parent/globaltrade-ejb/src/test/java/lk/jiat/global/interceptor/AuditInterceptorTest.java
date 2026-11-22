package lk.jiat.global.interceptor;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import lk.jiat.global.dao.AuditLogDAO;
import lk.jiat.global.entity.*;
import lk.jiat.global.service.*;
import lk.jiat.global.util.UserContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@DisplayName("Audit Interceptor Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditInterceptorTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "lk.jiat.global")
                .addAsWebInfResource("beans.xml", "beans.xml")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("org.hibernate.orm:hibernate-core")
                                .withTransitivity().asFile()
                )
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("org.slf4j:slf4j-simple")
                                .withTransitivity().asFile()
                );
    }

    @EJB
    private ShipmentServiceLocal shipmentService;

    @EJB
    private VendorServiceLocal vendorService;

    @EJB
    private WarehouseServiceLocal warehouseService;

    @EJB
    private CarrierServiceLocal carrierService;

    @EJB
    private AuditLogDAO auditLogDAO;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    private Warehouse warehouse;
    private Vendor vendor;
    private Carrier carrier;
    private User createdBy;

    @BeforeEach
    void setUp() throws Exception {
        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
            userTransaction.rollback();
        }

        userTransaction.begin();
        try {
            // ===== CLEAN UP ALL TEST DATA =====
            em.createQuery("DELETE FROM ShipmentStatusHistory").executeUpdate();
            em.createQuery("DELETE FROM ShipmentOrder").executeUpdate();
            em.createQuery("DELETE FROM ShipmentItem").executeUpdate();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createQuery("DELETE FROM AuditLog").executeUpdate();
            em.createQuery("DELETE FROM VendorPerformanceEvaluation").executeUpdate();
            em.createQuery("DELETE FROM Vendor").executeUpdate();
            em.createQuery("DELETE FROM Carrier").executeUpdate();
            em.createQuery("DELETE FROM Warehouse").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.flush();

            // ===== CREATE ROLE =====
            Role role = new Role("LOGISTICS_COORDINATOR", "LC Role");
            em.persist(role);
            em.flush();

            // ===== CREATE USER =====
            createdBy = new User("audituser", "hash", "audit@test.com", "Audit User", role);
            createdBy.setStatus("ACTIVE");
            em.persist(createdBy);
            em.flush();

            // ===== Set UserContext BEFORE any service calls =====
            UserContext.setCurrentUser(createdBy.getUserId(), createdBy.getUsername());

            // ===== CREATE TEST DATA =====
            warehouse = warehouseService.createWarehouse("Audit WH", "Location", "Sri Lanka", 10000);
            vendor = vendorService.createVendor("Audit Vendor", "audit@vendor.com", "123", "Sri Lanka", "ACTIVE");
            carrier = carrierService.createCarrier("Audit Carrier", "Standard", "audit@carrier.com");

            userTransaction.commit();

        } catch (Exception e) {
            userTransaction.rollback();
            throw e;
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        UserContext.clear();

        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
            userTransaction.commit();
        }

        userTransaction.begin();
        try {
            em.createQuery("DELETE FROM ShipmentStatusHistory").executeUpdate();
            em.createQuery("DELETE FROM ShipmentOrder").executeUpdate();
            em.createQuery("DELETE FROM ShipmentItem").executeUpdate();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createQuery("DELETE FROM AuditLog").executeUpdate();
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

    private void flushInTransaction() throws Exception {
        if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
            userTransaction.begin();
            em.flush();
            userTransaction.commit();
        } else {
            em.flush();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should create audit log on shipment creation")
    void auditInterceptor_ShouldLogShipmentCreation() throws Exception {
        if (createdBy == null || createdBy.getUserId() == null) {
            System.err.println("Skipping test: createdBy is null");
            return;
        }

        UserContext.setCurrentUser(createdBy.getUserId(), createdBy.getUsername());

        userTransaction.begin();
        long beforeCount = auditLogDAO.count();
        userTransaction.commit();

        Shipment shipment = shipmentService.createShipment(
                "AUDIT-SHIP-001",
                warehouse.getWarehouseId(),
                "Audit Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        flushInTransaction();
        Thread.sleep(500);

        userTransaction.begin();
        List<AuditLog> logs = auditLogDAO.findAll();
        userTransaction.commit();

        assertTrue(logs.size() > beforeCount, "Audit log count should increase");

        // Find the most recent CREATE audit log for SHIPMENT
        AuditLog log = logs.stream()
                .filter(l -> "CREATE".equals(l.getAction()) && "SHIPMENT".equals(l.getEntityType()))
                .max(Comparator.comparing(AuditLog::getLoggedAt))
                .orElse(null);

        assertNotNull(log, "Audit log should be created for shipment creation");
        assertNotNull(log.getUser(), "Audit log should have a user");
        assertEquals(createdBy.getUserId(), log.getUser().getUserId(), "User ID should match");
        assertNotNull(log.getDetails(), "Details should not be null");

        assertTrue(log.getDetails().contains("ShipmentService.createShipment"),
                "Details should contain method name: ShipmentService.createShipment");
        assertTrue(log.getDetails().contains(createdBy.getUsername()),
                "Details should contain username: " + createdBy.getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("Should create audit log on vendor creation")
    void auditInterceptor_ShouldLogVendorCreation() throws Exception {
        if (createdBy == null || createdBy.getUserId() == null) {
            System.err.println("Skipping test: createdBy is null");
            return;
        }

        UserContext.setCurrentUser(createdBy.getUserId(), createdBy.getUsername());

        userTransaction.begin();
        long beforeCount = auditLogDAO.count();
        userTransaction.commit();

        Vendor newVendor = vendorService.createVendor(
                "New Audit Vendor",
                "new@audit.com",
                "456",
                "Singapore",
                "ACTIVE"
        );

        flushInTransaction();
        Thread.sleep(500);

        userTransaction.begin();
        List<AuditLog> logs = auditLogDAO.findAll();
        userTransaction.commit();

        assertTrue(logs.size() > beforeCount, "Audit log count should increase");

        AuditLog log = logs.stream()
                .filter(l -> "CREATE".equals(l.getAction()) && "VENDOR".equals(l.getEntityType()))
                .max(Comparator.comparing(AuditLog::getLoggedAt))
                .orElse(null);

        assertNotNull(log, "Audit log should be created for vendor creation");
        assertNotNull(log.getDetails(), "Details should not be null");
        assertTrue(log.getDetails().contains("VendorService.createVendor"), "Details should contain vendor creation info");
    }

    @Test
    @Order(3)
    @DisplayName("Should create audit log on update operations")
    void auditInterceptor_ShouldLogUpdateOperations() throws Exception {
        if (createdBy == null || createdBy.getUserId() == null) {
            System.err.println("Skipping test: createdBy is null");
            return;
        }

        if (vendor == null || vendor.getVendorId() == null) {
            System.err.println("Skipping test: vendor is null");
            return;
        }

        UserContext.setCurrentUser(createdBy.getUserId(), createdBy.getUsername());

        userTransaction.begin();
        long beforeCount = auditLogDAO.count();
        userTransaction.commit();

        vendorService.updateVendor(
                vendor.getVendorId(),
                "Updated Vendor Name",
                vendor.getContactEmail(),
                vendor.getContactPhone(),
                vendor.getCountry(),
                "SUSPENDED"
        );

        flushInTransaction();
        Thread.sleep(500);

        userTransaction.begin();
        List<AuditLog> logs = auditLogDAO.findAll();
        userTransaction.commit();

        assertTrue(logs.size() > beforeCount, "Audit log count should increase");

        AuditLog log = logs.stream()
                .filter(l -> "UPDATE".equals(l.getAction()) && "VENDOR".equals(l.getEntityType()))
                .max(Comparator.comparing(AuditLog::getLoggedAt))
                .orElse(null);

        assertNotNull(log, "Audit log should be created for update operation");

        assertEquals(vendor.getVendorId(), log.getEntityId(), "Vendor ID should match");
        assertTrue(log.getDetails().contains("VendorService.updateVendor"), "Details should contain method name");
    }

    @Test
    @Order(4)
    @DisplayName("Should not create audit log for read-only methods")
    void auditInterceptor_ShouldSkipReadOnlyMethods() throws Exception {
        if (createdBy == null || createdBy.getUserId() == null) {
            System.err.println("Skipping test: createdBy is null");
            return;
        }

        UserContext.setCurrentUser(createdBy.getUserId(), createdBy.getUsername());

        userTransaction.begin();
        long beforeCount = auditLogDAO.count();
        userTransaction.commit();

        vendorService.findAllVendors();
        vendorService.findVendorById(vendor.getVendorId());

        userTransaction.begin();
        List<AuditLog> logs = auditLogDAO.findAll();
        userTransaction.commit();

        assertEquals(beforeCount, logs.size(), "Read-only methods should not create audit logs");
    }

    @Test
    @Order(5)
    @DisplayName("Should include user information in audit log")
    void auditInterceptor_ShouldIncludeUserInfo() throws Exception {
        if (createdBy == null || createdBy.getUserId() == null) {
            System.err.println("Skipping test: createdBy is null");
            return;
        }

        UserContext.setCurrentUser(createdBy.getUserId(), createdBy.getUsername());

        // Create a new warehouse
        Warehouse newWarehouse = warehouseService.createWarehouse("Audit WH 2", "Location 2", "India", 5000);

        flushInTransaction();
        Thread.sleep(500);

        userTransaction.begin();
        List<AuditLog> logs = auditLogDAO.findAll();
        userTransaction.commit();

        AuditLog log = logs.stream()
                .filter(l -> "CREATE".equals(l.getAction()) && "WAREHOUSE".equals(l.getEntityType()))
                .max(Comparator.comparing(AuditLog::getLoggedAt))
                .orElse(null);

        assertNotNull(log, "Audit log for warehouse should exist");
        assertNotNull(log.getUser(), "Audit log should have user");
        assertEquals(createdBy.getUserId(), log.getUser().getUserId(), "User ID should match");
        assertTrue(log.getDetails().contains(createdBy.getUsername()), "Details should contain username");
    }
}