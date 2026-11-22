package lk.jiat.global.service;

import lk.jiat.global.dao.*;
import lk.jiat.global.entity.*;
import lk.jiat.global.exception.*;
import lk.jiat.global.util.TrackingNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShipmentServiceTest {

    @Mock
    private ShipmentDAO shipmentDAO;

    @Mock
    private ShipmentItemDAO shipmentItemDAO;

    @Mock
    private ShipmentStatusHistoryDAO statusHistoryDAO;

    @Mock
    private WarehouseDAO warehouseDAO;

    @Mock
    private VendorDAO vendorDAO;

    @Mock
    private CarrierDAO carrierDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private InventoryItemDAO inventoryItemDAO;

    @Mock
    private CustomerOrderDAO customerOrderDAO;

    @Mock
    private OrderItemDAO orderItemDAO;

    @Mock
    private ShipmentOrderDAO shipmentOrderDAO;

    @Mock
    private CustomsDocumentDAO customsDocumentDAO;

    @Mock
    private EntityManager em;

    @InjectMocks
    private ShipmentService shipmentService;

    private Warehouse warehouse;
    private Vendor vendor;
    private Carrier carrier;
    private User createdBy;
    private Shipment shipment;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse("Colombo Hub", "Colombo", "Sri Lanka", 10000);
        warehouse.setWarehouseId(1);

        vendor = new Vendor("Hayleys Logistics", "hayleys@logistics.com", "Sri Lanka");
        vendor.setVendorId(1);
        vendor.setStatus("ACTIVE");

        carrier = new Carrier("DHL", "Air Freight", "info@dhl.com");
        carrier.setCarrierId(1);

        createdBy = new User("admin", "hash", "admin@globaltrade.com", "Admin User",
                new Role("LOGISTICS_COORDINATOR", ""));
        createdBy.setUserId(1);

        shipment = new Shipment();
        shipment.setShipmentId(1);
        shipment.setTrackingNumber("COL-HAY-DHL-20260101120000");
        shipment.setOriginWarehouse(warehouse);
        shipment.setVendor(vendor);
        shipment.setCarrier(carrier);
        shipment.setCreatedBy(createdBy);
        shipment.setStatus("PENDING");
        shipment.setScheduledDate(LocalDateTime.now().plusDays(1));
    }

    // ===== CREATE SHIPMENT TESTS =====

    @Test
    @DisplayName("Should create shipment successfully")
    void createShipment_ShouldSucceed() throws Exception {
        // Arrange
        String trackingNumber = "COL-HAY-DHL-20260101120000";
        LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);

        when(warehouseDAO.findById(1)).thenReturn(Optional.of(warehouse));
        when(vendorDAO.findById(1)).thenReturn(Optional.of(vendor));
        when(carrierDAO.findById(1)).thenReturn(Optional.of(carrier));
        when(userDAO.findById(1)).thenReturn(Optional.of(createdBy));
        when(shipmentDAO.findByTrackingNumber(trackingNumber)).thenReturn(null);

        // Act
        Shipment result = shipmentService.createShipment(
                trackingNumber, 1, "Singapore", 1, 1,
                "PENDING", scheduledDate, null, 1
        );

        // Assert
        assertNotNull(result);
        assertEquals(trackingNumber, result.getTrackingNumber());
        assertEquals(warehouse, result.getOriginWarehouse());
        assertEquals(vendor, result.getVendor());
        assertEquals(carrier, result.getCarrier());
        verify(shipmentDAO).create(any(Shipment.class));
        verify(statusHistoryDAO).create(any(ShipmentStatusHistory.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when required fields are missing")
    void createShipment_ShouldThrowValidationException_MissingFields() {
        assertThrows(ValidationException.class, () ->
                shipmentService.createShipment(null, null, null, null, null,
                        null, null, null, null)
        );
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when tracking number already exists")
    void createShipment_ShouldThrowDuplicateResourceException() throws Exception {
        String trackingNumber = "COL-HAY-DHL-20260101120000";

        when(warehouseDAO.findById(1)).thenReturn(Optional.of(warehouse));
        when(vendorDAO.findById(1)).thenReturn(Optional.of(vendor));
        when(carrierDAO.findById(1)).thenReturn(Optional.of(carrier));
        when(userDAO.findById(1)).thenReturn(Optional.of(createdBy));
        when(shipmentDAO.findByTrackingNumber(trackingNumber)).thenReturn(shipment);

        assertThrows(DuplicateResourceException.class, () ->
                shipmentService.createShipment(
                        trackingNumber, 1, "Singapore", 1, 1,
                        "PENDING", LocalDateTime.now().plusDays(1), null, 1
                )
        );
    }

    @Test
    @DisplayName("Should throw VendorSuspendedException when vendor is suspended")
    void createShipment_ShouldThrowVendorSuspendedException() throws Exception {
        vendor.setStatus("SUSPENDED");

        when(warehouseDAO.findById(1)).thenReturn(Optional.of(warehouse));
        when(vendorDAO.findById(1)).thenReturn(Optional.of(vendor));

        assertThrows(VendorSuspendedException.class, () ->
                shipmentService.createShipment(
                        "TRACK-001", 1, "Singapore", 1, 1,
                        "PENDING", LocalDateTime.now().plusDays(1), null, 1
                )
        );
    }

    // ===== STATUS TRANSITION TESTS =====

    @Test
    @DisplayName("Should allow valid status transition PENDING -> IN_TRANSIT")
    void updateShipmentStatus_ShouldAllowValidTransition() throws Exception {
        shipment.setStatus("PENDING");

        when(shipmentDAO.findById(1)).thenReturn(Optional.of(shipment));
        when(userDAO.findById(1)).thenReturn(Optional.of(createdBy));
        when(customsDocumentDAO.findByShipmentId(anyInt())).thenReturn(Collections.emptyList());

        shipmentService.updateShipmentStatus(1, "IN_TRANSIT", 1, "Started transit");

        assertEquals("IN_TRANSIT", shipment.getStatus());
        verify(statusHistoryDAO).create(any(ShipmentStatusHistory.class));
    }

    @Test
    @DisplayName("Should throw InvalidShipmentStateException for invalid transition")
    void updateShipmentStatus_ShouldThrowInvalidShipmentStateException() {
        shipment.setStatus("DELIVERED");

        when(shipmentDAO.findById(1)).thenReturn(Optional.of(shipment));

        assertThrows(InvalidShipmentStateException.class, () ->
                shipmentService.updateShipmentStatus(1, "IN_TRANSIT", 1, null)
        );
    }

    @Test
    @DisplayName("Should require customs approval before DELIVERED")
    void updateShipmentStatus_ShouldRequireCustomsApprovalForDelivered() throws Exception {
        shipment.setStatus("IN_TRANSIT");

        // No customs documents
        when(shipmentDAO.findById(1)).thenReturn(Optional.of(shipment));
        when(customsDocumentDAO.findByShipmentId(1)).thenReturn(Collections.emptyList());

        assertThrows(BusinessException.class, () ->
                shipmentService.updateShipmentStatus(1, "DELIVERED", 1, null)
        );
    }

    // ===== CANCELLATION TESTS =====

    @Test
    @DisplayName("Should cancel shipment with REQUIRES_NEW transaction")
    void cancelShipment_ShouldSucceed() throws Exception {
        shipment.setStatus("IN_TRANSIT");

        when(shipmentDAO.findById(1)).thenReturn(Optional.of(shipment));
        when(userDAO.findById(1)).thenReturn(Optional.of(createdBy));
        when(shipmentItemDAO.findByShipmentId(1)).thenReturn(Collections.emptyList());
        when(shipmentOrderDAO.findByShipmentId(1)).thenReturn(Collections.emptyList());

        shipmentService.cancelShipment(1, 1, "Cancelled by user");

        assertEquals("CANCELLED", shipment.getStatus());
        verify(statusHistoryDAO).create(any(ShipmentStatusHistory.class));
    }

    @Test
    @DisplayName("Should throw InvalidShipmentStateException when cancelling delivered shipment")
    void cancelShipment_ShouldThrowInvalidShipmentStateException() {
        shipment.setStatus("DELIVERED");

        when(shipmentDAO.findById(1)).thenReturn(Optional.of(shipment));

        assertThrows(InvalidShipmentStateException.class, () ->
                shipmentService.cancelShipment(1, 1, null)
        );
    }

    // ===== INVENTORY MANAGEMENT TESTS =====

    @Test
    @DisplayName("Should add shipment item if sufficient inventory")
    void addShipmentItem_ShouldSucceed() throws Exception {
        InventoryItem item = new InventoryItem("SKU-001", "Product A", warehouse, 100, 10,
                BigDecimal.valueOf(100.0));
        item.setItemId(1);

        shipment.setStatus("IN_TRANSIT");

        when(shipmentDAO.findById(1)).thenReturn(Optional.of(shipment));
        when(inventoryItemDAO.findById(1)).thenReturn(Optional.of(item));
        when(shipmentItemDAO.findByShipmentId(1)).thenReturn(Collections.emptyList());

        ShipmentItem result = shipmentService.addShipmentItem(1, 1, 10);

        assertNotNull(result);
        assertEquals(10, result.getQuantity());
        verify(inventoryItemDAO).update(item);
        assertEquals(90, item.getQuantity());
    }

    @Test
    @DisplayName("Should throw InsufficientInventoryException when not enough stock")
    void addShipmentItem_ShouldThrowInsufficientInventoryException() throws Exception {
        InventoryItem item = new InventoryItem("SKU-001", "Product A", warehouse, 5, 10,
                BigDecimal.valueOf(100.0));
        item.setItemId(1);

        shipment.setStatus("IN_TRANSIT");

        when(shipmentDAO.findById(1)).thenReturn(Optional.of(shipment));
        when(inventoryItemDAO.findById(1)).thenReturn(Optional.of(item));

        assertThrows(InsufficientInventoryException.class, () ->
                shipmentService.addShipmentItem(1, 1, 10)
        );
    }

    @Test
    @DisplayName("Should throw ValidationException when adding items to delivered shipment")
    void addShipmentItem_ShouldThrowValidationException_DeliveredShipment() throws Exception {
        shipment.setStatus("DELIVERED");

        when(shipmentDAO.findById(1)).thenReturn(Optional.of(shipment));

        assertThrows(ValidationException.class, () ->
                shipmentService.addShipmentItem(1, 1, 10)
        );
    }

    // ===== STATUS TRANSITION VALIDATION TESTS =====

    @Test
    @DisplayName("Should validate valid status transitions")
    void isValidStatusTransition_ShouldReturnTrueForValidTransitions() {
        assertTrue(shipmentService.isValidStatusTransition("PENDING", "IN_TRANSIT"));
        assertTrue(shipmentService.isValidStatusTransition("PENDING", "CANCELLED"));
        assertTrue(shipmentService.isValidStatusTransition("IN_TRANSIT", "DELIVERED"));
        assertTrue(shipmentService.isValidStatusTransition("IN_TRANSIT", "DELAYED"));
        assertTrue(shipmentService.isValidStatusTransition("IN_TRANSIT", "CANCELLED"));
        assertTrue(shipmentService.isValidStatusTransition("DELAYED", "IN_TRANSIT"));
        assertTrue(shipmentService.isValidStatusTransition("DELAYED", "DELIVERED"));
    }

    @Test
    @DisplayName("Should reject invalid status transitions")
    void isValidStatusTransition_ShouldReturnFalseForInvalidTransitions() {
        assertFalse(shipmentService.isValidStatusTransition("PENDING", "DELIVERED"));
        assertFalse(shipmentService.isValidStatusTransition("DELIVERED", "IN_TRANSIT"));
        assertFalse(shipmentService.isValidStatusTransition("CANCELLED", "IN_TRANSIT"));
        assertFalse(shipmentService.isValidStatusTransition(null, "IN_TRANSIT"));
        assertFalse(shipmentService.isValidStatusTransition("PENDING", null));
    }

    // ===== FIND SHIPMENTS BY ORDER ID =====

    @Test
    @DisplayName("Should find shipments by order ID")
    void findShipmentsByOrderId_ShouldReturnShipments() throws Exception {
        Integer orderId = 1;

        when(em.createQuery(anyString(), eq(Shipment.class)))
                .thenReturn(mock(jakarta.persistence.TypedQuery.class));

        List<Shipment> result = shipmentService.findShipmentsByOrderId(orderId);

        assertNotNull(result);
        // If no shipments, returns empty list
    }

    // ===== TRACKING NUMBER UNIQUENESS =====

    @Test
    @DisplayName("Should return true if tracking number is unique")
    void isTrackingNumberUnique_ShouldReturnTrue_WhenUnique() {
        String trackingNumber = "UNIQUE-001";
        when(shipmentDAO.findByTrackingNumber(trackingNumber)).thenReturn(null);

        assertTrue(shipmentService.isTrackingNumberUnique(trackingNumber, null));
    }

    @Test
    @DisplayName("Should return false if tracking number already exists")
    void isTrackingNumberUnique_ShouldReturnFalse_WhenExists() {
        String trackingNumber = "EXISTING-001";
        when(shipmentDAO.findByTrackingNumber(trackingNumber)).thenReturn(shipment);

        assertFalse(shipmentService.isTrackingNumberUnique(trackingNumber, null));
    }

    @Test
    @DisplayName("Should return true if tracking number belongs to same shipment (exclude check)")
    void isTrackingNumberUnique_ShouldReturnTrue_WhenExcludingSameShipment() {
        String trackingNumber = "EXISTING-001";
        shipment.setShipmentId(1);
        when(shipmentDAO.findByTrackingNumber(trackingNumber)).thenReturn(shipment);

        assertTrue(shipmentService.isTrackingNumberUnique(trackingNumber, 1));
    }
}