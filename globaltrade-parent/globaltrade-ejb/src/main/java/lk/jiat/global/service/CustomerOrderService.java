package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.CustomerDAO;
import lk.jiat.global.dao.CustomerOrderDAO;
import lk.jiat.global.dao.InventoryItemDAO;
import lk.jiat.global.dao.OrderItemDAO;
import lk.jiat.global.dao.ShipmentOrderDAO;
import lk.jiat.global.entity.Customer;
import lk.jiat.global.entity.CustomerOrder;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.OrderItem;
import lk.jiat.global.exception.*;
import lk.jiat.global.interceptor.AuditLogged;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.interceptor.PerformanceLogged;
import lk.jiat.global.util.AppLogger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Stateless
@AuditLogged(skipReadOnly = true)
@ExceptionLogged
@PermitAll
public class CustomerOrderService implements CustomerOrderServiceLocal {

    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "ASSIGNED", "FULFILLED", "CANCELLED");

    @Inject
    private CustomerOrderDAO customerOrderDAO;

    @Inject
    private OrderItemDAO orderItemDAO;

    @Inject
    private ShipmentOrderDAO shipmentOrderDAO;

    @Inject
    private CustomerDAO customerDAO;

    @Inject
    private InventoryItemDAO inventoryItemDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PerformanceLogged(threshold = 500)
    public CustomerOrder createOrder(Integer customerId, String destination, String notes,
                                     Map<Integer, Integer> items)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException, BusinessException {

        AppLogger.info("Creating order for customer: {}", customerId);

        if (customerId == null) {
            throw new ValidationException("Customer ID is required");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new ValidationException("Destination is required");
        }
        if (items == null || items.isEmpty()) {
            throw new ValidationException("At least one item is required");
        }

        Customer customer = customerDAO.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        // Validate items
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            Integer itemId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity == null || quantity <= 0) {
                throw new ValidationException("Quantity for item " + itemId + " must be greater than 0");
            }

            InventoryItem item = inventoryItemDAO.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

            if (item.getQuantity() < quantity) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory for item: " + item.getName() +
                                ". Available: " + item.getQuantity() + ", Requested: " + quantity +
                                ". Order cannot be created."
                );
            }
        }

        // Create order
        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setDestination(destination.trim());
        order.setStatus("PENDING");
        order.setRequestedDate(LocalDateTime.now());
        order.setNotes(notes != null ? notes.trim() : null);

        customerOrderDAO.create(order);

        // Create order items
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            Integer itemId = entry.getKey();
            Integer quantity = entry.getValue();

            InventoryItem item = inventoryItemDAO.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

            // Reserve inventory immediately (reduce quantity)
            item.setQuantity(item.getQuantity() - quantity);
            inventoryItemDAO.update(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(quantity);
            orderItemDAO.create(orderItem);
        }

        AppLogger.info("Order created with ID: {}", order.getOrderId());
        return order;
    }

    @Override
    public List<CustomerOrder> getOrdersForCustomer(Integer customerId) throws ResourceNotFoundException {
        if (!customerDAO.findById(customerId).isPresent()) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        return customerOrderDAO.findByCustomerId(customerId);
    }

    @Override
    public Optional<CustomerOrder> findOrderById(Integer orderId) {
        return customerOrderDAO.findById(orderId);
    }

    @Override
    public List<CustomerOrder> getPendingOrders() throws ResourceNotFoundException {
        return customerOrderDAO.findPendingOrders();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PerformanceLogged(threshold = 500)
    public CustomerOrder cancelOrder(Integer orderId, Integer customerId, String reason)
            throws ResourceNotFoundException, ValidationException, BusinessException {

        AppLogger.info("Cancelling order: {} by customer: {}", orderId, customerId);

        CustomerOrder order = customerOrderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!order.getCustomer().getCustomerId().equals(customerId)) {
            throw new ValidationException("You can only cancel your own orders");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new ValidationException("Only PENDING orders can be cancelled. Current status: " + order.getStatus());
        }

        // Release inventory
        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            InventoryItem item = orderItem.getItem();
            item.setQuantity(item.getQuantity() + orderItem.getQuantity());
            inventoryItemDAO.update(item);
        }

        order.setStatus("CANCELLED");
        order.setNotes(reason != null ? order.getNotes() + "\nCancelled: " + reason : "Cancelled");

        customerOrderDAO.update(order);

        AppLogger.info("Order cancelled: {}", orderId);
        return order;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignOrderToShipment(Integer orderId, Integer shipmentId)
            throws ResourceNotFoundException, ValidationException, BusinessException {

        AppLogger.info("Assigning order {} to shipment {}", orderId, shipmentId);

        CustomerOrder order = customerOrderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!"PENDING".equals(order.getStatus())) {
            throw new ValidationException("Only PENDING orders can be assigned. Current status: " + order.getStatus());
        }

        order.setStatus("ASSIGNED");
        customerOrderDAO.update(order);

        AppLogger.info("Order {} assigned to shipment {}", orderId, shipmentId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void fulfillOrder(Integer orderId) throws ResourceNotFoundException, ValidationException {
        AppLogger.info("Fulfilling order: {}", orderId);

        CustomerOrder order = customerOrderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!"ASSIGNED".equals(order.getStatus())) {
            throw new ValidationException("Only ASSIGNED orders can be fulfilled. Current status: " + order.getStatus());
        }

        order.setStatus("FULFILLED");
        customerOrderDAO.update(order);

        AppLogger.info("Order fulfilled: {}", orderId);
    }

    @Override
    public List<OrderItem> getOrderItems(Integer orderId) throws ResourceNotFoundException {
        if (!customerOrderDAO.findById(orderId).isPresent()) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        return orderItemDAO.findByOrderId(orderId);
    }

    @Override
    public List<CustomerOrder> getOrdersByShipment(Integer shipmentId) throws ResourceNotFoundException {
        return customerOrderDAO.findByShipmentId(shipmentId);
    }

    @Override
    public boolean isOrderOwnedByCustomer(Integer orderId, Integer customerId) {
        try {
            CustomerOrder order = customerOrderDAO.findById(orderId).orElse(null);
            return order != null && order.getCustomer().getCustomerId().equals(customerId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        // Valid transitions
        if ("PENDING".equals(currentStatus) && ("ASSIGNED".equals(newStatus) || "CANCELLED".equals(newStatus))) {
            return true;
        }
        if ("ASSIGNED".equals(currentStatus) && "FULFILLED".equals(newStatus)) {
            return true;
        }
        return false;
    }

    @Override
    public int getOrderItemCount(Integer orderId) throws ResourceNotFoundException {
        if (!customerOrderDAO.findById(orderId).isPresent()) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        return orderItemDAO.countByOrderId(orderId);
    }
}