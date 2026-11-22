package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.entity.ShipmentItem;
import lk.jiat.global.entity.ShipmentStatusHistory;
import lk.jiat.global.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Local
public interface ShipmentServiceLocal {

    // ===== CREATE SHIPMENT (Original - No Orders) =====
    Shipment createShipment(String trackingNumber, Integer originWarehouseId,
                            String destination, Integer vendorId, Integer carrierId,
                            String status, LocalDateTime scheduledDate,
                            LocalDateTime estimatedDeliveryDate, Integer createdByUserId)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException,
            BusinessException, VendorSuspendedException;

    // ===== CREATE SHIPMENT WITH ORDER ATTACHMENT (Phase 6 Extended) =====
    Shipment createShipment(String trackingNumber, Integer originWarehouseId,
                            String destination, Integer vendorId, Integer carrierId,
                            String status, LocalDateTime scheduledDate,
                            LocalDateTime estimatedDeliveryDate, Integer createdByUserId,
                            List<Integer> orderIds)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException,
            BusinessException, VendorSuspendedException, InsufficientInventoryException;

    // ===== UPDATE SHIPMENT =====
    Shipment updateShipment(Integer shipmentId, String trackingNumber, Integer originWarehouseId,
                            String destination, Integer vendorId, Integer carrierId,
                            String status, LocalDateTime scheduledDate,
                            LocalDateTime estimatedDeliveryDate)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException,
            VendorSuspendedException;

    // ===== FIND METHODS =====
    Optional<Shipment> findShipmentById(Integer shipmentId);

    Optional<Shipment> findShipmentByTrackingNumber(String trackingNumber);

    List<Shipment> findAllShipments();

    List<Shipment> findShipmentsByStatus(String status);

    List<Shipment> findShipmentsByVendor(Integer vendorId);

    List<Shipment> findShipmentsByWarehouse(Integer warehouseId);

    List<Shipment> findShipmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    List<Shipment> findDelayedShipments();

    // ===== FIND SHIPMENTS BY ORDER ID (Phase 6 Extended) =====
    List<Shipment> findShipmentsByOrderId(Integer orderId);

    // ===== DELETE SHIPMENT =====
    void deleteShipment(Integer shipmentId) throws ResourceNotFoundException;

    // ===== STATUS MANAGEMENT =====
    Shipment updateShipmentStatus(Integer shipmentId, String newStatus,
                                  Integer changedByUserId, String remarks)
            throws ResourceNotFoundException, ValidationException, BusinessException;

    Shipment cancelShipment(Integer shipmentId, Integer changedByUserId, String remarks)
            throws ResourceNotFoundException, ValidationException, BusinessException;

    List<ShipmentStatusHistory> getStatusHistory(Integer shipmentId) throws ResourceNotFoundException;

    boolean isValidStatusTransition(String currentStatus, String newStatus);

    // ===== SHIPMENT ITEMS =====
    ShipmentItem addShipmentItem(Integer shipmentId, Integer itemId, Integer quantity)
            throws ResourceNotFoundException, ValidationException, BusinessException;

    void removeShipmentItem(Long shipmentItemId) throws ResourceNotFoundException, BusinessException;

    List<ShipmentItem> getShipmentItems(Integer shipmentId) throws ResourceNotFoundException;

    int getTotalItemsForShipment(Integer shipmentId) throws ResourceNotFoundException;

    // ===== INVENTORY MANAGEMENT =====
    void reserveInventory(Integer shipmentId) throws ResourceNotFoundException, BusinessException;

    void releaseInventory(Integer shipmentId) throws ResourceNotFoundException, BusinessException;

    // ===== VALIDATION =====
    boolean isTrackingNumberUnique(String trackingNumber, Integer excludeShipmentId);

    boolean hasApprovedCustomsDocument(Integer shipmentId);
}