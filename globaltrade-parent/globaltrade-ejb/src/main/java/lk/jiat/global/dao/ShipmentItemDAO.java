package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.ShipmentItem;

import java.util.List;

@Stateless
public class ShipmentItemDAO extends GenericDAO<ShipmentItem, Long> {

    public ShipmentItemDAO() {
        super(ShipmentItem.class);
    }

    public List<ShipmentItem> findByShipmentId(Integer shipmentId) {
        return em.createQuery("SELECT si FROM ShipmentItem si WHERE si.shipment.shipmentId = :shipmentId", ShipmentItem.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }

    public List<ShipmentItem> findByItemId(Integer itemId) {
        return em.createQuery("SELECT si FROM ShipmentItem si WHERE si.item.itemId = :itemId", ShipmentItem.class)
                .setParameter("itemId", itemId)
                .getResultList();
    }

    public List<ShipmentItem> findByShipmentIdWithDetails(Integer shipmentId) {
        return em.createQuery("SELECT si FROM ShipmentItem si JOIN FETCH si.item WHERE si.shipment.shipmentId = :shipmentId", ShipmentItem.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }

    public int getTotalItemsForShipment(Integer shipmentId) {
        try {
            Long count = em.createQuery("SELECT SUM(si.quantity) FROM ShipmentItem si WHERE si.shipment.shipmentId = :shipmentId", Long.class)
                    .setParameter("shipmentId", shipmentId)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}