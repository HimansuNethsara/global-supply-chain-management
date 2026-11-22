package lk.jiat.global.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Local;
import lk.jiat.global.entity.CustomerOrder;
import lk.jiat.global.entity.OrderItem;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Local
public interface CustomerOrderServiceLocal {

    CustomerOrder createOrder(Integer customerId, String destination, String notes,
                              Map<Integer, Integer> items) // itemId -> quantity
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException, BusinessException;

    List<CustomerOrder> getOrdersForCustomer(Integer customerId) throws ResourceNotFoundException;

    Optional<CustomerOrder> findOrderById(Integer orderId);

    List<CustomerOrder> getPendingOrders() throws ResourceNotFoundException;

    CustomerOrder cancelOrder(Integer orderId, Integer customerId, String reason)
            throws ResourceNotFoundException, ValidationException, BusinessException;

    void assignOrderToShipment(Integer orderId, Integer shipmentId)
            throws ResourceNotFoundException, ValidationException, BusinessException;

    void fulfillOrder(Integer orderId) throws ResourceNotFoundException, ValidationException;

    List<OrderItem> getOrderItems(Integer orderId) throws ResourceNotFoundException;

    List<CustomerOrder> getOrdersByShipment(Integer shipmentId) throws ResourceNotFoundException;

    boolean isOrderOwnedByCustomer(Integer orderId, Integer customerId);

    boolean isValidStatusTransition(String currentStatus, String newStatus);

    int getOrderItemCount(Integer orderId) throws ResourceNotFoundException;
}