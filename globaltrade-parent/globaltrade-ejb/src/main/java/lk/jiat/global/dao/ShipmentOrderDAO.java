package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.ShipmentOrder;

import java.util.List;

@Stateless
public class ShipmentOrderDAO extends GenericDAO<ShipmentOrder, Integer> {

    public ShipmentOrderDAO() {
        super(ShipmentOrder.class);
    }

    public List<ShipmentOrder> findByShipmentId(Integer shipmentId) {
        return em.createQuery(
                        "SELECT so FROM ShipmentOrder so " +
                                "JOIN FETCH so.order " +
                                "JOIN FETCH so.order.customer " +
                                "WHERE so.shipment.shipmentId = :shipmentId", ShipmentOrder.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }

    public List<ShipmentOrder> findByOrderId(Integer orderId) {
        return em.createQuery(
                        "SELECT so FROM ShipmentOrder so " +
                                "JOIN FETCH so.shipment " +
                                "WHERE so.order.orderId = :orderId", ShipmentOrder.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<ShipmentOrder> findByShipmentIdWithOrders(Integer shipmentId) {
        return em.createQuery(
                        "SELECT so FROM ShipmentOrder so " +
                                "JOIN FETCH so.order " +
                                "JOIN FETCH so.order.customer " +
                                "JOIN FETCH so.order.customer.user " +
                                "WHERE so.shipment.shipmentId = :shipmentId", ShipmentOrder.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }
}