package lk.jiat.global.service;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import lk.jiat.global.entity.*;
import lk.jiat.global.exception.*;
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
@DisplayName("Shipment Service Integration Tests")
class ShipmentServiceArquillianTest {

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
    private ShipmentServiceLocal shipmentService;

    @EJB
    private VendorServiceLocal vendorService;

    @EJB
    private WarehouseServiceLocal warehouseService;

    @EJB
    private CarrierServiceLocal carrierService;

    @EJB
    private UserServiceLocal userService;

    @EJB
    private InventoryServiceLocal inventoryService;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    private Warehouse warehouse;
    private Vendor vendor;
    private Carrier carrier;
    private User createdBy;
    private InventoryItem item;

    @BeforeEach
    void setUp() throws Exception {
        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
            userTransaction.rollback();
        }

        userTransaction.begin();
        try {
            em.createQuery("DELETE FROM ShipmentItem").executeUpdate();
            em.createQuery("DELETE FROM ShipmentOrder").executeUpdate();
            em.createQuery("DELETE FROM ShipmentStatusHistory").executeUpdate();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createQuery("DELETE FROM InventoryItem").executeUpdate();
            em.createQuery("DELETE FROM CustomerOrder").executeUpdate();
            em.createQuery("DELETE FROM OrderItem").executeUpdate();
            em.createQuery("DELETE FROM VendorPerformanceEvaluation").executeUpdate();
            em.createQuery("DELETE FROM Vendor").executeUpdate();
            em.createQuery("DELETE FROM Carrier").executeUpdate();
            em.createQuery("DELETE FROM Warehouse").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.flush();

            // Create Role
            Role adminRole = new Role("LOGISTICS_COORDINATOR", "Admin Role");
            em.persist(adminRole);
            em.flush();

            // Create User
            String username = "admin_" + System.currentTimeMillis();
            createdBy = new User(username, "hash", "admin@test.com", "Admin User", adminRole);
            createdBy.setStatus("ACTIVE");
            em.persist(createdBy);
            em.flush();

            // Create test data
            warehouse = warehouseService.createWarehouse("Test Warehouse", "Test Location", "Sri Lanka", 10000);
            vendor = vendorService.createVendor("Test Vendor", "test@vendor.com", "1234567890", "Sri Lanka", "ACTIVE");
            carrier = carrierService.createCarrier("Test Carrier", "Standard", "test@carrier.com");
            item = inventoryService.createItem(
                    "TEST-SKU-001",
                    "Test Item",
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
            em.createQuery("DELETE FROM ShipmentItem").executeUpdate();
            em.createQuery("DELETE FROM ShipmentOrder").executeUpdate();
            em.createQuery("DELETE FROM ShipmentStatusHistory").executeUpdate();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createQuery("DELETE FROM InventoryItem").executeUpdate();
            em.createQuery("DELETE FROM CustomerOrder").executeUpdate();
            em.createQuery("DELETE FROM OrderItem").executeUpdate();
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
    // 1. CREATE SHIPMENT TESTS
    // ============================================================

    @Test
    @DisplayName("Should create a shipment successfully")
    void createShipment_ShouldSucceed() throws Exception {
        String trackingNumber = "TEST-" + System.currentTimeMillis();
        LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);

        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                scheduledDate,
                scheduledDate.plusDays(2),
                createdBy.getUserId()
        );

        assertNotNull(shipment);
        assertNotNull(shipment.getShipmentId());
        assertEquals(trackingNumber, shipment.getTrackingNumber());
        assertEquals("PENDING", shipment.getStatus());

        userTransaction.begin();
        Shipment found = em.find(Shipment.class, shipment.getShipmentId());
        userTransaction.commit();

        assertNotNull(found);
        assertEquals(trackingNumber, found.getTrackingNumber());
        assertEquals("PENDING", found.getStatus());
    }

    @Test
    @DisplayName("Should throw ValidationException when creating shipment with missing fields")
    void createShipment_ShouldThrowValidationException() {
        assertThrows(ValidationException.class, () ->
                shipmentService.createShipment(
                        null, null, null, null, null,
                        null, null, null, null
                )
        );
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when tracking number already exists")
    void createShipment_ShouldThrowDuplicateResourceException() throws Exception {
        String trackingNumber = "DUPLICATE-" + System.currentTimeMillis();

        shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Destination 1",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        assertThrows(DuplicateResourceException.class, () ->
                shipmentService.createShipment(
                        trackingNumber,
                        warehouse.getWarehouseId(),
                        "Destination 2",
                        vendor.getVendorId(),
                        carrier.getCarrierId(),
                        "PENDING",
                        LocalDateTime.now().plusDays(1),
                        null,
                        createdBy.getUserId()
                )
        );
    }

    // ============================================================
    // 2. STATUS TRANSITION TESTS
    // ============================================================

    @Test
    @DisplayName("Should update shipment status from PENDING to IN_TRANSIT")
    void updateShipmentStatus_ShouldUpdateToInTransit() throws Exception {
        String trackingNumber = "TRANSIT-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        assertEquals("PENDING", shipment.getStatus());

        // Update status
        Shipment updated = shipmentService.updateShipmentStatus(
                shipment.getShipmentId(),
                "IN_TRANSIT",
                createdBy.getUserId(),
                "Shipment in transit"
        );

        assertEquals("IN_TRANSIT", updated.getStatus());

        userTransaction.begin();
        Shipment found = em.find(Shipment.class, shipment.getShipmentId());
        userTransaction.commit();

        assertNotNull(found);
        assertEquals("IN_TRANSIT", found.getStatus());
    }

    @Test
    @DisplayName("Should throw BusinessException when trying to deliver without customs approval")
    void updateShipmentStatus_ShouldThrowBusinessExceptionForDelivered() throws Exception {
        String trackingNumber = "INVALID-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        // PENDING -> DELIVERED fails customs check first, so throws BusinessException
        assertThrows(BusinessException.class, () ->
                shipmentService.updateShipmentStatus(
                        shipment.getShipmentId(),
                        "DELIVERED",
                        createdBy.getUserId(),
                        "Invalid transition"
                )
        );
    }

    @Test
    @DisplayName("Should require customs approval before DELIVERED from IN_TRANSIT")
    void updateShipmentStatus_ShouldRequireCustomsApproval() throws Exception {
        String trackingNumber = "CUSTOMS-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        Shipment inTransit = shipmentService.updateShipmentStatus(
                shipment.getShipmentId(),
                "IN_TRANSIT",
                createdBy.getUserId(),
                "In transit"
        );
        assertEquals("IN_TRANSIT", inTransit.getStatus());

        // Try to deliver without customs approval - should throw BusinessException
        assertThrows(BusinessException.class, () ->
                shipmentService.updateShipmentStatus(
                        shipment.getShipmentId(),
                        "DELIVERED",
                        createdBy.getUserId(),
                        "Deliver without customs"
                )
        );
    }

    // ============================================================
    // 3. CANCELLATION TESTS
    // ============================================================

    @Test
    @DisplayName("Should cancel a shipment successfully")
    void cancelShipment_ShouldSucceed() throws Exception {
        String trackingNumber = "CANCEL-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        // Cancel the shipment
        Shipment cancelled = shipmentService.cancelShipment(
                shipment.getShipmentId(),
                createdBy.getUserId(),
                "Cancelled by user"
        );

        assertEquals("CANCELLED", cancelled.getStatus());

        userTransaction.begin();
        Shipment found = em.find(Shipment.class, shipment.getShipmentId());
        userTransaction.commit();

        assertNotNull(found);
        assertEquals("CANCELLED", found.getStatus());
    }

    // ============================================================
    // 4. INVENTORY MANAGEMENT TESTS
    // ============================================================

    @Test
    @DisplayName("Should add shipment item and deduct inventory")
    void addShipmentItem_ShouldDeductInventory() throws Exception {
        String trackingNumber = "ITEM-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        ShipmentItem shipmentItem = shipmentService.addShipmentItem(
                shipment.getShipmentId(),
                item.getItemId(),
                10
        );

        assertNotNull(shipmentItem);
        assertEquals(10, shipmentItem.getQuantity());

        userTransaction.begin();
        InventoryItem updatedItem = em.find(InventoryItem.class, item.getItemId());
        userTransaction.commit();

        assertNotNull(updatedItem);
        assertEquals(90, updatedItem.getQuantity());
    }

    @Test
    @DisplayName("Should throw InsufficientInventoryException when not enough stock")
    void addShipmentItem_ShouldThrowInsufficientInventoryException() throws Exception {
        String trackingNumber = "INSUFFICIENT-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        assertThrows(InsufficientInventoryException.class, () ->
                shipmentService.addShipmentItem(
                        shipment.getShipmentId(),
                        item.getItemId(),
                        200
                )
        );
    }

    // ============================================================
    // 5. FIND METHODS TESTS
    // ============================================================

    @Test
    @DisplayName("Should find shipment by tracking number")
    void findShipmentByTrackingNumber_ShouldSucceed() throws Exception {
        String trackingNumber = "FIND-" + System.currentTimeMillis();
        Shipment created = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        Optional<Shipment> found = shipmentService.findShipmentByTrackingNumber(trackingNumber);

        assertTrue(found.isPresent());
        assertEquals(created.getShipmentId(), found.get().getShipmentId());
        assertEquals(trackingNumber, found.get().getTrackingNumber());
    }

    @Test
    @DisplayName("Should find all shipments")
    void findAllShipments_ShouldReturnList() throws Exception {
        String tracking1 = "ALL-" + System.currentTimeMillis();
        String tracking2 = "ALL-" + System.currentTimeMillis() + 1;

        shipmentService.createShipment(
                tracking1,
                warehouse.getWarehouseId(),
                "Destination 1",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );
        shipmentService.createShipment(
                tracking2,
                warehouse.getWarehouseId(),
                "Destination 2",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        List<Shipment> shipments = shipmentService.findAllShipments();

        assertTrue(shipments.size() >= 2);
    }

    @Test
    @DisplayName("Should find shipments by status")
    void findShipmentsByStatus_ShouldReturnFilteredList() throws Exception {
        String tracking1 = "PENDING-" + System.currentTimeMillis();
        String tracking2 = "TRANSIT-" + System.currentTimeMillis();

        Shipment pending = shipmentService.createShipment(
                tracking1,
                warehouse.getWarehouseId(),
                "Destination 1",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        Shipment inTransit = shipmentService.createShipment(
                tracking2,
                warehouse.getWarehouseId(),
                "Destination 2",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        shipmentService.updateShipmentStatus(
                inTransit.getShipmentId(),
                "IN_TRANSIT",
                createdBy.getUserId(),
                "In transit"
        );

        List<Shipment> pendingShipments = shipmentService.findShipmentsByStatus("PENDING");
        List<Shipment> inTransitShipments = shipmentService.findShipmentsByStatus("IN_TRANSIT");

        assertTrue(pendingShipments.stream().anyMatch(s -> s.getShipmentId().equals(pending.getShipmentId())));
        assertTrue(inTransitShipments.stream().anyMatch(s -> s.getShipmentId().equals(inTransit.getShipmentId())));
    }

    // ============================================================
    // 6. TRANSACTION TESTS
    // ============================================================

    @Test
    @DisplayName("Should rollback entire shipment creation if any step fails")
    void createShipment_ShouldRollbackOnFailure() {
        String trackingNumber = "ROLLBACK-" + System.currentTimeMillis();

        assertThrows(ResourceNotFoundException.class, () ->
                shipmentService.createShipment(
                        trackingNumber,
                        warehouse.getWarehouseId(),
                        "Destination",
                        999, // Invalid vendor ID
                        carrier.getCarrierId(),
                        "PENDING",
                        LocalDateTime.now().plusDays(1),
                        null,
                        createdBy.getUserId()
                )
        );

        Optional<Shipment> found = shipmentService.findShipmentByTrackingNumber(trackingNumber);
        assertFalse(found.isPresent(), "Shipment should not exist due to rollback");
    }

    // ============================================================
    // 7. CUSTOMS APPROVAL CHECK TEST
    // ============================================================

    @Test
    @DisplayName("Should check if shipment has approved customs documents")
    void hasApprovedCustomsDocument_ShouldReturnCorrectValue() throws Exception {
        String trackingNumber = "CUSTOMS-CHECK-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        boolean hasApproved = shipmentService.hasApprovedCustomsDocument(shipment.getShipmentId());
        assertFalse(hasApproved);
    }

    // ============================================================
    // 8. STATUS HISTORY TEST - SIMPLIFIED
    // ============================================================

    @Test
    @DisplayName("Should maintain status history")
    void getStatusHistory_ShouldReturnHistory() throws Exception {
        String trackingNumber = "HISTORY-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        // Verify initial status
        assertEquals("PENDING", shipment.getStatus());

        // Update to IN_TRANSIT
        Shipment inTransit = shipmentService.updateShipmentStatus(
                shipment.getShipmentId(),
                "IN_TRANSIT",
                createdBy.getUserId(),
                "Started"
        );
        assertEquals("IN_TRANSIT", inTransit.getStatus());

        List<ShipmentStatusHistory> history = shipmentService.getStatusHistory(shipment.getShipmentId());

        assertTrue(history.size() >= 2, "History should have at least 2 entries");

        boolean foundInTransit = history.stream()
                .anyMatch(h -> "IN_TRANSIT".equals(h.getNewStatus()) && "Started".equals(h.getRemarks()));
        assertTrue(foundInTransit, "Should find IN_TRANSIT entry with 'Started' remarks");
    }

    // ============================================================
    // 9. SHIPMENT ITEMS TESTS
    // ============================================================

    @Test
    @DisplayName("Should get shipment items list")
    void getShipmentItems_ShouldReturnItems() throws Exception {
        String trackingNumber = "ITEMS-LIST-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        shipmentService.addShipmentItem(shipment.getShipmentId(), item.getItemId(), 5);

        List<ShipmentItem> items = shipmentService.getShipmentItems(shipment.getShipmentId());

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(item.getItemId(), items.get(0).getItem().getItemId());
        assertEquals(5, items.get(0).getQuantity());
    }

    @Test
    @DisplayName("Should remove shipment item and restore inventory")
    void removeShipmentItem_ShouldRestoreInventory() throws Exception {
        String trackingNumber = "REMOVE-ITEM-" + System.currentTimeMillis();
        Shipment shipment = shipmentService.createShipment(
                trackingNumber,
                warehouse.getWarehouseId(),
                "Test Destination",
                vendor.getVendorId(),
                carrier.getCarrierId(),
                "PENDING",
                LocalDateTime.now().plusDays(1),
                null,
                createdBy.getUserId()
        );

        ShipmentItem shipmentItem = shipmentService.addShipmentItem(
                shipment.getShipmentId(),
                item.getItemId(),
                10
        );
        Long itemId = shipmentItem.getId();

        // Verify inventory was deducted
        userTransaction.begin();
        InventoryItem beforeRemove = em.find(InventoryItem.class, item.getItemId());
        userTransaction.commit();
        assertNotNull(beforeRemove);
        assertEquals(90, beforeRemove.getQuantity());

        // Remove item
        shipmentService.removeShipmentItem(itemId);

        // Inventory should be restored
        userTransaction.begin();
        InventoryItem afterRemove = em.find(InventoryItem.class, item.getItemId());
        userTransaction.commit();
        assertNotNull(afterRemove);
        assertEquals(100, afterRemove.getQuantity());

        // Item should be removed from shipment
        List<ShipmentItem> items = shipmentService.getShipmentItems(shipment.getShipmentId());
        assertTrue(items.isEmpty());
    }
}