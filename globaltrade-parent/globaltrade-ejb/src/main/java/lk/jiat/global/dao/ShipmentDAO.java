package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.Shipment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class ShipmentDAO extends GenericDAO<Shipment, Integer> {

    public ShipmentDAO() {
        super(Shipment.class);
    }

    @Override
    public Optional<Shipment> findById(Integer id) {
        try {
            Shipment shipment = em.createQuery(
                            "SELECT s FROM Shipment s " +
                                    "LEFT JOIN FETCH s.originWarehouse " +
                                    "LEFT JOIN FETCH s.vendor " +
                                    "LEFT JOIN FETCH s.carrier " +
                                    "LEFT JOIN FETCH s.createdBy " +
                                    "WHERE s.shipmentId = :id", Shipment.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(shipment);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Shipment findByTrackingNumber(String trackingNumber) {
        try {
            return em.createQuery(
                            "SELECT s FROM Shipment s " +
                                    "LEFT JOIN FETCH s.originWarehouse " +
                                    "LEFT JOIN FETCH s.vendor " +
                                    "LEFT JOIN FETCH s.carrier " +
                                    "LEFT JOIN FETCH s.createdBy " +
                                    "WHERE s.trackingNumber = :trackingNumber", Shipment.class)
                    .setParameter("trackingNumber", trackingNumber)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Shipment> findAll() {
        return em.createQuery(
                        "SELECT s FROM Shipment s " +
                                "LEFT JOIN FETCH s.originWarehouse " +
                                "LEFT JOIN FETCH s.vendor " +
                                "LEFT JOIN FETCH s.carrier " +
                                "LEFT JOIN FETCH s.createdBy", Shipment.class)
                .getResultList();
    }

    public List<Shipment> findByStatus(String status) {
        return em.createQuery(
                        "SELECT s FROM Shipment s " +
                                "LEFT JOIN FETCH s.originWarehouse " +
                                "LEFT JOIN FETCH s.vendor " +
                                "LEFT JOIN FETCH s.carrier " +
                                "LEFT JOIN FETCH s.createdBy " +
                                "WHERE s.status = :status", Shipment.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Shipment> findByVendorId(Integer vendorId) {
        return em.createQuery(
                        "SELECT s FROM Shipment s " +
                                "LEFT JOIN FETCH s.originWarehouse " +
                                "LEFT JOIN FETCH s.vendor " +
                                "LEFT JOIN FETCH s.carrier " +
                                "LEFT JOIN FETCH s.createdBy " +
                                "WHERE s.vendor.vendorId = :vendorId", Shipment.class)
                .setParameter("vendorId", vendorId)
                .getResultList();
    }

    public List<Shipment> findByWarehouseId(Integer warehouseId) {
        return em.createQuery(
                        "SELECT s FROM Shipment s " +
                                "LEFT JOIN FETCH s.originWarehouse " +
                                "LEFT JOIN FETCH s.vendor " +
                                "LEFT JOIN FETCH s.carrier " +
                                "LEFT JOIN FETCH s.createdBy " +
                                "WHERE s.originWarehouse.warehouseId = :warehouseId", Shipment.class)
                .setParameter("warehouseId", warehouseId)
                .getResultList();
    }

    public List<Shipment> findShipmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery(
                        "SELECT s FROM Shipment s " +
                                "LEFT JOIN FETCH s.originWarehouse " +
                                "LEFT JOIN FETCH s.vendor " +
                                "LEFT JOIN FETCH s.carrier " +
                                "LEFT JOIN FETCH s.createdBy " +
                                "WHERE s.scheduledDate BETWEEN :startDate AND :endDate", Shipment.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    public List<Shipment> findDelayedShipments() {
        return em.createQuery(
                        "SELECT s FROM Shipment s " +
                                "LEFT JOIN FETCH s.originWarehouse " +
                                "LEFT JOIN FETCH s.vendor " +
                                "LEFT JOIN FETCH s.carrier " +
                                "LEFT JOIN FETCH s.createdBy " +
                                "WHERE s.status = 'DELAYED'", Shipment.class)
                .getResultList();
    }
}