package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.ShipmentStatusHistory;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ShipmentStatusHistoryDAO extends GenericDAO<ShipmentStatusHistory, Long> {

    public ShipmentStatusHistoryDAO() {
        super(ShipmentStatusHistory.class);
    }

    public List<ShipmentStatusHistory> findByShipmentId(Integer shipmentId) {
        return em.createQuery("SELECT h FROM ShipmentStatusHistory h WHERE h.shipment.shipmentId = :shipmentId ORDER BY h.changedAt DESC",
                        ShipmentStatusHistory.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }

    public List<ShipmentStatusHistory> findByStatus(String status) {
        return em.createQuery("SELECT h FROM ShipmentStatusHistory h WHERE h.newStatus = :status ORDER BY h.changedAt DESC",
                        ShipmentStatusHistory.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<ShipmentStatusHistory> findChangesBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery("SELECT h FROM ShipmentStatusHistory h WHERE h.changedAt BETWEEN :startDate AND :endDate ORDER BY h.changedAt DESC",
                        ShipmentStatusHistory.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}