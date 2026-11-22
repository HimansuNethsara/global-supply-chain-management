package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.jiat.global.dao.*;
import lk.jiat.global.entity.*;
import lk.jiat.global.exception.*;
import lk.jiat.global.interceptor.AuditLogged;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.interceptor.PerformanceLogged;
import lk.jiat.global.interceptor.StatusChange;
import lk.jiat.global.util.AppLogger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Stateless
@AuditLogged(skipReadOnly = true)
@ExceptionLogged
public class ShipmentService implements ShipmentServiceLocal {

    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
            "PENDING", "IN_TRANSIT", "DELIVERED", "DELAYED", "CANCELLED"
    ));

    private static final Set<String> VALID_TRANSITIONS = new HashSet<>(Arrays.asList(
            "PENDING->IN_TRANSIT", "PENDING->CANCELLED",
            "IN_TRANSIT->DELAYED", "IN_TRANSIT->CANCELLED",
            "IN_TRANSIT->DELIVERED",
            "DELAYED->IN_TRANSIT", "DELAYED->CANCELLED",
            "DELAYED->DELIVERED"
    ));

    @Inject
    private ShipmentDAO shipmentDAO;

    @Inject
    private ShipmentItemDAO shipmentItemDAO;

    @Inject
    private ShipmentStatusHistoryDAO statusHistoryDAO;

    @Inject
    private WarehouseDAO warehouseDAO;

    @Inject
    private VendorDAO vendorDAO;

    @Inject
    private CarrierDAO carrierDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private InventoryItemDAO inventoryItemDAO;

    @Inject
    private CustomerOrderDAO customerOrderDAO;

    @Inject
    private OrderItemDAO orderItemDAO;

    @Inject
    private ShipmentOrderDAO shipmentOrderDAO;

    @Inject
    private CustomsDocumentDAO customsDocumentDAO;

    @PersistenceContext(unitName = "SupplyChainPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Shipment createShipment(String trackingNumber, Integer originWarehouseId,
                                   String destination, Integer vendorId, Integer carrierId,
                                   String status, LocalDateTime scheduledDate,
                                   LocalDateTime estimatedDeliveryDate, Integer createdByUserId)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException,
            BusinessException, VendorSuspendedException {

        AppLogger.info("Creating shipment with tracking number: {}", trackingNumber);

        validateRequiredFields(trackingNumber, originWarehouseId, destination, vendorId, scheduledDate, createdByUserId);

        if (!isTrackingNumberUnique(trackingNumber.trim(), null)) {
            throw new DuplicateResourceException("Tracking number '" + trackingNumber + "' already exists");
        }

        Warehouse warehouse = verifyWarehouse(originWarehouseId);
        Vendor vendor = verifyVendor(vendorId);
        User createdBy = verifyUser(createdByUserId);
        Carrier carrier = verifyCarrier(carrierId);

        String finalStatus = validateAndGetStatus(status);

        Shipment shipment = buildShipment(trackingNumber, warehouse, destination, vendor, carrier,
                finalStatus, scheduledDate, estimatedDeliveryDate, createdBy);

        shipmentDAO.create(shipment);

        createStatusHistory(shipment, finalStatus, createdBy, "Shipment created");

        AppLogger.info("Shipment created successfully with ID: {}", shipment.getShipmentId());
        return shipment;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PerformanceLogged(threshold = 1000)
    public Shipment createShipment(String trackingNumber, Integer originWarehouseId,
                                   String destination, Integer vendorId, Integer carrierId,
                                   String status, LocalDateTime scheduledDate,
                                   LocalDateTime estimatedDeliveryDate, Integer createdByUserId,
                                   List<Integer> orderIds)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException,
            BusinessException, VendorSuspendedException, InsufficientInventoryException {

        AppLogger.info("Creating shipment with tracking number: {} and {} orders",
                trackingNumber, orderIds != null ? orderIds.size() : 0);

        validateRequiredFieldsWithCarrier(trackingNumber, originWarehouseId, destination,
                vendorId, carrierId, scheduledDate, createdByUserId);

        if (!isTrackingNumberUnique(trackingNumber.trim(), null)) {
            throw new DuplicateResourceException("Tracking number '" + trackingNumber + "' already exists");
        }

        Warehouse warehouse = verifyWarehouse(originWarehouseId);
        Vendor vendor = verifyVendor(vendorId);
        User createdBy = verifyUser(createdByUserId);
        Carrier carrier = verifyCarrier(carrierId);

        String finalStatus = validateAndGetStatus(status);

        Shipment shipment = buildShipment(trackingNumber, warehouse, destination, vendor, carrier,
                finalStatus, scheduledDate, estimatedDeliveryDate, createdBy);

        shipmentDAO.create(shipment);

        createStatusHistory(shipment, finalStatus, createdBy, "Shipment created");

        if (orderIds != null && !orderIds.isEmpty()) {
            attachOrdersToShipment(shipment, orderIds, createdBy);
        }

        AppLogger.info("Shipment created successfully with ID: {} and {} attached orders",
                shipment.getShipmentId(), orderIds != null ? orderIds.size() : 0);
        return shipment;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @StatusChange
    public Shipment updateShipment(Integer shipmentId, String trackingNumber, Integer originWarehouseId,
                                   String destination, Integer vendorId, Integer carrierId,
                                   String status, LocalDateTime scheduledDate,
                                   LocalDateTime estimatedDeliveryDate)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException,
            VendorSuspendedException {

        AppLogger.info("Updating shipment with ID: {}", shipmentId);

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            if (!isTrackingNumberUnique(trackingNumber.trim(), shipmentId)) {
                throw new DuplicateResourceException("Tracking number '" + trackingNumber + "' already exists");
            }
            shipment.setTrackingNumber(trackingNumber.trim());
        }

        if (originWarehouseId != null) {
            shipment.setOriginWarehouse(verifyWarehouse(originWarehouseId));
        }

        if (destination != null && !destination.trim().isEmpty()) {
            shipment.setDestination(destination.trim());
        }

        if (vendorId != null) {
            shipment.setVendor(verifyVendor(vendorId));
        }

        if (carrierId != null) {
            shipment.setCarrier(verifyCarrier(carrierId));
        }

        if (status != null && !status.trim().isEmpty()) {
            if (!VALID_STATUSES.contains(status)) {
                throw new ValidationException("Invalid status: " + status);
            }
            shipment.setStatus(status);
        }

        if (scheduledDate != null) {
            shipment.setScheduledDate(scheduledDate);
        }

        if (estimatedDeliveryDate != null) {
            shipment.setEstimatedDeliveryDate(estimatedDeliveryDate);
        }

        return shipmentDAO.update(shipment);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @StatusChange
    @PerformanceLogged(threshold = 500)
    public Shipment updateShipmentStatus(Integer shipmentId, String newStatus,
                                         Integer changedByUserId, String remarks)
            throws ResourceNotFoundException, ValidationException, BusinessException,
            InvalidShipmentStateException {

        AppLogger.info("Updating shipment status: {} -> {}", shipmentId, newStatus);

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        if (newStatus == null || !VALID_STATUSES.contains(newStatus)) {
            throw new ValidationException("Invalid status: " + newStatus);
        }

        String currentStatus = shipment.getStatus();

        if ("DELIVERED".equals(newStatus)) {
            if (!hasApprovedCustomsDocument(shipmentId)) {
                throw new BusinessException(
                        "Cannot deliver shipment. Customs documents must be approved first."
                );
            }
            AppLogger.info("Shipment {} has customs approval, allowing delivery", shipmentId);
        }

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidShipmentStateException(
                    "Invalid status transition: " + currentStatus + " -> " + newStatus
            );
        }

        if ("DELIVERED".equals(newStatus) && shipment.getActualDeliveryDate() == null) {
            shipment.setActualDeliveryDate(LocalDateTime.now());
        }

        User changedBy = null;
        if (changedByUserId != null) {
            changedBy = userDAO.findById(changedByUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + changedByUserId));
        }

        String oldStatus = shipment.getStatus();
        shipment.setStatus(newStatus);
        shipmentDAO.update(shipment);

        ShipmentStatusHistory history = new ShipmentStatusHistory();
        history.setShipment(shipment);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        history.setRemarks(remarks);
        statusHistoryDAO.create(history);

        if ("DELIVERED".equals(newStatus)) {
            fulfillLinkedOrders(shipmentId);
        }

        AppLogger.info("Shipment status updated: {} -> {}", oldStatus, newStatus);
        return shipment;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @StatusChange
    public Shipment cancelShipment(Integer shipmentId, Integer changedByUserId, String remarks)
            throws ResourceNotFoundException, ValidationException, BusinessException,
            InvalidShipmentStateException {

        AppLogger.info("Cancelling shipment with ID: {}", shipmentId);

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        String currentStatus = shipment.getStatus();

        if ("DELIVERED".equals(currentStatus)) {
            throw new InvalidShipmentStateException(
                    "Cannot cancel a delivered shipment. Shipment ID: " + shipmentId
            );
        }
        if ("CANCELLED".equals(currentStatus)) {
            throw new InvalidShipmentStateException(
                    "Shipment is already cancelled. Shipment ID: " + shipmentId
            );
        }

        releaseInventory(shipmentId);

        User changedBy = null;
        if (changedByUserId != null) {
            changedBy = userDAO.findById(changedByUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + changedByUserId));
        }

        String oldStatus = shipment.getStatus();
        shipment.setStatus("CANCELLED");
        shipmentDAO.update(shipment);

        ShipmentStatusHistory history = new ShipmentStatusHistory();
        history.setShipment(shipment);
        history.setOldStatus(oldStatus);
        history.setNewStatus("CANCELLED");
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        history.setRemarks(remarks != null ? remarks : "Shipment cancelled");
        statusHistoryDAO.create(history);

        try {
            List<ShipmentOrder> shipmentOrders = shipmentOrderDAO.findByShipmentId(shipmentId);
            for (ShipmentOrder so : shipmentOrders) {
                CustomerOrder order = so.getOrder();
                if ("ASSIGNED".equals(order.getStatus())) {
                    order.setStatus("PENDING");
                    customerOrderDAO.update(order);
                    AppLogger.info("Order {} released back to PENDING due to shipment cancellation", order.getOrderId());
                }
            }
        } catch (Exception e) {
            AppLogger.error("Failed to release orders for cancelled shipment {}: {}", shipmentId, e.getMessage());
        }

        AppLogger.info("Shipment cancelled: {}", shipmentId);
        return shipment;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ShipmentItem addShipmentItem(Integer shipmentId, Integer itemId, Integer quantity)
            throws ResourceNotFoundException, ValidationException, BusinessException,
            InsufficientInventoryException {

        AppLogger.info("Adding item to shipment: {} -> {}", shipmentId, itemId);

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        if ("DELIVERED".equals(shipment.getStatus()) || "CANCELLED".equals(shipment.getStatus())) {
            throw new ValidationException("Cannot add items to a " + shipment.getStatus() + " shipment");
        }

        InventoryItem item = inventoryItemDAO.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be greater than 0");
        }

        if (!item.getWarehouse().getWarehouseId().equals(shipment.getOriginWarehouse().getWarehouseId())) {
            throw new ValidationException("Item is not in the shipment's origin warehouse");
        }

        if (item.getQuantity() < quantity) {
            throw new InsufficientInventoryException(
                    "Insufficient inventory for item: " + item.getName() +
                            ". Available: " + item.getQuantity() + ", Requested: " + quantity
            );
        }

        List<ShipmentItem> existingItems = shipmentItemDAO.findByShipmentId(shipmentId);
        for (ShipmentItem existing : existingItems) {
            if (existing.getItem().getItemId().equals(itemId)) {
                existing.setQuantity(existing.getQuantity() + quantity);
                shipmentItemDAO.update(existing);

                item.setQuantity(item.getQuantity() - quantity);
                inventoryItemDAO.update(item);

                AppLogger.info("Updated existing shipment item quantity to: {}", existing.getQuantity());
                return existing;
            }
        }

        ShipmentItem shipmentItem = new ShipmentItem();
        shipmentItem.setShipment(shipment);
        shipmentItem.setItem(item);
        shipmentItem.setQuantity(quantity);
        shipmentItemDAO.create(shipmentItem);

        item.setQuantity(item.getQuantity() - quantity);
        inventoryItemDAO.update(item);

        AppLogger.info("Item added to shipment: {} units of {}", quantity, item.getName());
        return shipmentItem;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeShipmentItem(Long shipmentItemId) throws ResourceNotFoundException, BusinessException {
        AppLogger.info("Removing shipment item: {}", shipmentItemId);

        ShipmentItem shipmentItem = shipmentItemDAO.findById(shipmentItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment item not found with ID: " + shipmentItemId));

        Shipment shipment = shipmentItem.getShipment();

        if ("DELIVERED".equals(shipment.getStatus()) || "CANCELLED".equals(shipment.getStatus())) {
            throw new ValidationException("Cannot remove items from a " + shipment.getStatus() + " shipment");
        }

        InventoryItem item = shipmentItem.getItem();
        item.setQuantity(item.getQuantity() + shipmentItem.getQuantity());
        inventoryItemDAO.update(item);

        shipmentItemDAO.delete(shipmentItem);
        AppLogger.info("Shipment item removed: {}", shipmentItemId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteShipment(Integer shipmentId) throws ResourceNotFoundException {
        AppLogger.info("Deleting shipment with ID: {}", shipmentId);

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        List<ShipmentItem> items = shipmentItemDAO.findByShipmentId(shipmentId);
        for (ShipmentItem item : items) {
            shipmentItemDAO.delete(item);
        }

        List<ShipmentStatusHistory> history = statusHistoryDAO.findByShipmentId(shipmentId);
        for (ShipmentStatusHistory h : history) {
            statusHistoryDAO.delete(h);
        }

        List<ShipmentOrder> shipmentOrders = shipmentOrderDAO.findByShipmentId(shipmentId);
        for (ShipmentOrder so : shipmentOrders) {
            shipmentOrderDAO.delete(so);
        }

        shipmentDAO.delete(shipment);
        AppLogger.info("Shipment deleted successfully: {}", shipmentId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void reserveInventory(Integer shipmentId) throws ResourceNotFoundException, BusinessException {
        AppLogger.info("Reserving inventory for shipment: {}", shipmentId);

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        List<ShipmentItem> items = shipmentItemDAO.findByShipmentId(shipmentId);

        for (ShipmentItem shipmentItem : items) {
            InventoryItem item = shipmentItem.getItem();

            if (item.getQuantity() < shipmentItem.getQuantity()) {
                throw new BusinessException("Insufficient inventory for item: " + item.getName() +
                        ". Available: " + item.getQuantity() + ", Required: " + shipmentItem.getQuantity());
            }

            item.setQuantity(item.getQuantity() - shipmentItem.getQuantity());
            inventoryItemDAO.update(item);
        }

        AppLogger.info("Inventory reserved for shipment: {}", shipmentId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void releaseInventory(Integer shipmentId) throws ResourceNotFoundException, BusinessException {
        AppLogger.info("Releasing inventory for shipment: {}", shipmentId);

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        List<ShipmentItem> items = shipmentItemDAO.findByShipmentId(shipmentId);

        for (ShipmentItem shipmentItem : items) {
            InventoryItem item = shipmentItem.getItem();
            item.setQuantity(item.getQuantity() + shipmentItem.getQuantity());
            inventoryItemDAO.update(item);
        }

        AppLogger.info("Inventory released for shipment: {}", shipmentId);
    }

    @Override
    public Optional<Shipment> findShipmentById(Integer shipmentId) {
        return shipmentDAO.findById(shipmentId);
    }

    @Override
    public Optional<Shipment> findShipmentByTrackingNumber(String trackingNumber) {
        return Optional.ofNullable(shipmentDAO.findByTrackingNumber(trackingNumber));
    }

    @Override
    public List<Shipment> findAllShipments() {
        return shipmentDAO.findAll();
    }

    @Override
    public List<Shipment> findShipmentsByStatus(String status) {
        return shipmentDAO.findByStatus(status);
    }

    @Override
    public List<Shipment> findShipmentsByVendor(Integer vendorId) {
        return shipmentDAO.findByVendorId(vendorId);
    }

    @Override
    public List<Shipment> findShipmentsByWarehouse(Integer warehouseId) {
        return shipmentDAO.findByWarehouseId(warehouseId);
    }

    @Override
    public List<Shipment> findShipmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return shipmentDAO.findShipmentsBetweenDates(startDate, endDate);
    }

    @Override
    public List<Shipment> findShipmentsByOrderId(Integer orderId) {
        try {
            return em.createQuery(
                            "SELECT so.shipment FROM ShipmentOrder so " +
                                    "LEFT JOIN FETCH so.shipment.originWarehouse " +
                                    "LEFT JOIN FETCH so.shipment.vendor " +
                                    "LEFT JOIN FETCH so.shipment.carrier " +
                                    "LEFT JOIN FETCH so.shipment.createdBy " +
                                    "WHERE so.order.orderId = :orderId " +
                                    "ORDER BY so.shipment.createdAt DESC", Shipment.class)
                    .setParameter("orderId", orderId)
                    .getResultList();
        } catch (Exception e) {
            AppLogger.warning("Failed to find shipments for order {}: {}", orderId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<ShipmentStatusHistory> getStatusHistory(Integer shipmentId) throws ResourceNotFoundException {
        if (!shipmentDAO.findById(shipmentId).isPresent()) {
            throw new ResourceNotFoundException("Shipment not found with ID: " + shipmentId);
        }
        return statusHistoryDAO.findByShipmentId(shipmentId);
    }

    @Override
    public List<ShipmentItem> getShipmentItems(Integer shipmentId) throws ResourceNotFoundException {
        if (!shipmentDAO.findById(shipmentId).isPresent()) {
            throw new ResourceNotFoundException("Shipment not found with ID: " + shipmentId);
        }
        return shipmentItemDAO.findByShipmentIdWithDetails(shipmentId);
    }

    @Override
    public int getTotalItemsForShipment(Integer shipmentId) throws ResourceNotFoundException {
        if (!shipmentDAO.findById(shipmentId).isPresent()) {
            throw new ResourceNotFoundException("Shipment not found with ID: " + shipmentId);
        }
        return shipmentItemDAO.getTotalItemsForShipment(shipmentId);
    }

    @Override
    public List<Shipment> findDelayedShipments() {
        return shipmentDAO.findDelayedShipments();
    }

    @Override
    public boolean isTrackingNumberUnique(String trackingNumber, Integer excludeShipmentId) {
        Shipment existing = shipmentDAO.findByTrackingNumber(trackingNumber);
        if (existing == null) {
            return true;
        }
        if (excludeShipmentId != null && existing.getShipmentId().equals(excludeShipmentId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasApprovedCustomsDocument(Integer shipmentId) {
        try {
            List<CustomsDocument> docs = customsDocumentDAO.findByShipmentId(shipmentId);
            for (CustomsDocument doc : docs) {
                if ("APPROVED".equals(doc.getStatus())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            AppLogger.warning("Failed to check customs documents for shipment {}: {}", shipmentId, e.getMessage());
            return false;
        }
    }

    @Override
    @PermitAll
    public boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        if ("DELIVERED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            return false;
        }

        if (currentStatus.equals(newStatus)) {
            return true;
        }

        String transition = currentStatus + "->" + newStatus;
        return VALID_TRANSITIONS.contains(transition);
    }

    private void validateRequiredFields(String trackingNumber, Integer originWarehouseId,
                                        String destination, Integer vendorId,
                                        LocalDateTime scheduledDate, Integer createdByUserId)
            throws ValidationException {

        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new ValidationException("Tracking number is required");
        }
        if (originWarehouseId == null) {
            throw new ValidationException("Origin warehouse is required");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new ValidationException("Destination is required");
        }
        if (vendorId == null) {
            throw new ValidationException("Vendor is required");
        }
        if (scheduledDate == null) {
            throw new ValidationException("Scheduled date is required");
        }
        if (createdByUserId == null) {
            throw new ValidationException("Created by user is required");
        }
    }

    private void validateRequiredFieldsWithCarrier(String trackingNumber, Integer originWarehouseId,
                                                   String destination, Integer vendorId, Integer carrierId,
                                                   LocalDateTime scheduledDate, Integer createdByUserId)
            throws ValidationException {

        validateRequiredFields(trackingNumber, originWarehouseId, destination, vendorId, scheduledDate, createdByUserId);

        if (carrierId == null) {
            throw new ValidationException("Carrier is required when attaching orders");
        }
    }

    private Warehouse verifyWarehouse(Integer warehouseId) throws ResourceNotFoundException {
        return warehouseDAO.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));
    }

    private Vendor verifyVendor(Integer vendorId) throws ResourceNotFoundException, VendorSuspendedException {
        Vendor vendor = vendorDAO.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));
        if (!"ACTIVE".equals(vendor.getStatus())) {
            throw new VendorSuspendedException(
                    "Vendor '" + vendor.getVendorName() + "' (ID: " + vendorId +
                            ") is currently " + vendor.getStatus().toLowerCase() +
                            ". Please select an active vendor for this shipment."
            );
        }
        return vendor;
    }

    private User verifyUser(Integer userId) throws ResourceNotFoundException {
        return userDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private Carrier verifyCarrier(Integer carrierId) throws ResourceNotFoundException {
        if (carrierId == null) {
            return null;
        }
        return carrierDAO.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with ID: " + carrierId));
    }

    private String validateAndGetStatus(String status) throws ValidationException {
        if (status == null || status.trim().isEmpty()) {
            return "PENDING";
        }
        String trimmedStatus = status.trim();
        if (!VALID_STATUSES.contains(trimmedStatus)) {
            throw new ValidationException("Invalid status: " + trimmedStatus);
        }
        if ("DELIVERED".equals(trimmedStatus) || "CANCELLED".equals(trimmedStatus)) {
            throw new ValidationException("Cannot create a shipment with status: " + trimmedStatus +
                    ". Use PENDING or IN_TRANSIT instead.");
        }
        return trimmedStatus;
    }

    private Shipment buildShipment(String trackingNumber, Warehouse warehouse, String destination,
                                   Vendor vendor, Carrier carrier, String status,
                                   LocalDateTime scheduledDate, LocalDateTime estimatedDeliveryDate,
                                   User createdBy) {

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(trackingNumber.trim());
        shipment.setOriginWarehouse(warehouse);
        shipment.setDestination(destination.trim());
        shipment.setVendor(vendor);
        shipment.setCarrier(carrier);
        shipment.setStatus(status);
        shipment.setScheduledDate(scheduledDate);
        shipment.setEstimatedDeliveryDate(estimatedDeliveryDate);
        shipment.setCreatedBy(createdBy);
        shipment.setCreatedAt(LocalDateTime.now());

        return shipment;
    }

    private void createStatusHistory(Shipment shipment, String status, User changedBy, String remarks) {
        ShipmentStatusHistory history = new ShipmentStatusHistory();
        history.setShipment(shipment);
        history.setNewStatus(status);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        history.setRemarks(remarks);
        statusHistoryDAO.create(history);
    }

    private void attachOrdersToShipment(Shipment shipment, List<Integer> orderIds, User createdBy)
            throws BusinessException, ResourceNotFoundException, ValidationException, InsufficientInventoryException {

        AppLogger.info("Attaching {} orders to shipment {}", orderIds.size(), shipment.getShipmentId());

        for (Integer orderId : orderIds) {
            CustomerOrder order = customerOrderDAO.findById(orderId)
                    .orElseThrow(() -> new BusinessException("Order not found with ID: " + orderId));

            if (!"PENDING".equals(order.getStatus())) {
                throw new OrderAlreadyAssignedException(
                        "Order " + orderId + " is not in PENDING status. Current: " + order.getStatus() +
                                ". Cannot assign to shipment."
                );
            }

            ShipmentOrder shipmentOrder = new ShipmentOrder();
            shipmentOrder.setShipment(shipment);
            shipmentOrder.setOrder(order);
            shipmentOrderDAO.create(shipmentOrder);

            order.setStatus("ASSIGNED");
            customerOrderDAO.update(order);

            AppLogger.info("Order {} assigned to shipment {}", orderId, shipment.getShipmentId());

            List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
            for (OrderItem orderItem : orderItems) {
                InventoryItem item = orderItem.getItem();

                if (!item.getWarehouse().getWarehouseId().equals(shipment.getOriginWarehouse().getWarehouseId())) {
                    throw new ValidationException(
                            "Item '" + item.getName() + "' is not in the shipment's origin warehouse. " +
                                    "Item is in: " + item.getWarehouse().getName()
                    );
                }

                if (item.getQuantity() < orderItem.getQuantity()) {
                    throw new InsufficientInventoryException(
                            "Insufficient inventory for item: " + item.getName() +
                                    ". Available: " + item.getQuantity() + ", Required: " + orderItem.getQuantity() +
                                    ". Order #" + orderId + " cannot be fulfilled."
                    );
                }

                boolean exists = shipmentItemDAO.findByShipmentId(shipment.getShipmentId())
                        .stream()
                        .anyMatch(si -> si.getItem().getItemId().equals(item.getItemId()));

                if (!exists) {
                    ShipmentItem shipmentItem = new ShipmentItem();
                    shipmentItem.setShipment(shipment);
                    shipmentItem.setItem(item);
                    shipmentItem.setQuantity(orderItem.getQuantity());
                    shipmentItemDAO.create(shipmentItem);

                    AppLogger.info("Added order item {} (qty: {}) to shipment {}",
                            item.getName(), orderItem.getQuantity(), shipment.getShipmentId());
                } else {
                    ShipmentItem existing = shipmentItemDAO.findByShipmentId(shipment.getShipmentId())
                            .stream()
                            .filter(si -> si.getItem().getItemId().equals(item.getItemId()))
                            .findFirst()
                            .orElse(null);

                    if (existing != null) {
                        existing.setQuantity(existing.getQuantity() + orderItem.getQuantity());
                        shipmentItemDAO.update(existing);
                        AppLogger.info("Updated existing shipment item quantity to: {}", existing.getQuantity());
                    }
                }

                item.setQuantity(item.getQuantity() - orderItem.getQuantity());
                inventoryItemDAO.update(item);
                AppLogger.info("Inventory deducted for item {}: {} remaining",
                        item.getName(), item.getQuantity());
            }
        }
    }

    private void fulfillLinkedOrders(Integer shipmentId) {
        try {
            List<CustomerOrder> linkedOrders = customerOrderDAO.findByShipmentId(shipmentId);
            for (CustomerOrder order : linkedOrders) {
                if ("ASSIGNED".equals(order.getStatus())) {
                    order.setStatus("FULFILLED");
                    customerOrderDAO.update(order);
                    AppLogger.info("Order {} fulfilled via shipment {} delivery", order.getOrderId(), shipmentId);
                }
            }
        } catch (Exception e) {
            AppLogger.error("Failed to fulfill linked orders for shipment {}: {}", shipmentId, e.getMessage());
        }
    }
}