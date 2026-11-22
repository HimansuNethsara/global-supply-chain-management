package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.CustomerOrder;

import java.util.List;
import java.util.Optional;

@Stateless
public class CustomerOrderDAO extends GenericDAO<CustomerOrder, Integer> {

    public CustomerOrderDAO() {
        super(CustomerOrder.class);
    }

    @Override
    public Optional<CustomerOrder> findById(Integer id) {
        try {
            CustomerOrder order = em.createQuery(
                            "SELECT o FROM CustomerOrder o " +
                                    "LEFT JOIN FETCH o.customer " +
                                    "LEFT JOIN FETCH o.customer.user " +
                                    "WHERE o.orderId = :id", CustomerOrder.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(order);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<CustomerOrder> findByCustomerId(Integer customerId) {
        return em.createQuery(
                        "SELECT o FROM CustomerOrder o " +
                                "LEFT JOIN FETCH o.customer " +
                                "LEFT JOIN FETCH o.customer.user " +
                                "WHERE o.customer.customerId = :customerId " +
                                "ORDER BY o.requestedDate DESC", CustomerOrder.class)
                .setParameter("customerId", customerId)
                .getResultList();
    }

    public List<CustomerOrder> findByStatus(String status) {
        return em.createQuery(
                        "SELECT o FROM CustomerOrder o " +
                                "LEFT JOIN FETCH o.customer " +
                                "LEFT JOIN FETCH o.customer.user " +
                                "WHERE o.status = :status " +
                                "ORDER BY o.requestedDate DESC", CustomerOrder.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<CustomerOrder> findPendingOrders() {
        return em.createQuery(
                        "SELECT o FROM CustomerOrder o " +
                                "LEFT JOIN FETCH o.customer " +
                                "LEFT JOIN FETCH o.customer.user " +
                                "WHERE o.status = 'PENDING' " +
                                "ORDER BY o.requestedDate ASC", CustomerOrder.class)
                .getResultList();
    }

    public List<CustomerOrder> findByShipmentId(Integer shipmentId) {
        return em.createQuery(
                        "SELECT o FROM CustomerOrder o " +
                                "LEFT JOIN FETCH o.customer " +
                                "LEFT JOIN FETCH o.customer.user " +
                                "WHERE o.orderId IN " +
                                "(SELECT so.order.orderId FROM ShipmentOrder so WHERE so.shipment.shipmentId = :shipmentId)",
                        CustomerOrder.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }
}