package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.RoutePlan;
import lk.jiat.global.util.AppLogger;

import java.util.List;

@Stateless
public class RoutePlanDAO extends GenericDAO<RoutePlan, Integer> {

    public RoutePlanDAO() {
        super(RoutePlan.class);
    }

    public List<RoutePlan> findByShipmentId(Integer shipmentId) {
        return em.createQuery(
                        "SELECT r FROM RoutePlan r " +
                                "LEFT JOIN FETCH r.shipment " +
                                "LEFT JOIN FETCH r.shipment.originWarehouse " +
                                "LEFT JOIN FETCH r.shipment.vendor " +
                                "WHERE r.shipment.shipmentId = :shipmentId " +
                                "ORDER BY r.calculatedAt DESC",
                        RoutePlan.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }

    public RoutePlan findLatestByShipmentId(Integer shipmentId) {
        try {
            List<RoutePlan> results = em.createQuery(
                            "SELECT r FROM RoutePlan r " +
                                    "LEFT JOIN FETCH r.shipment " +
                                    "LEFT JOIN FETCH r.shipment.originWarehouse " +
                                    "LEFT JOIN FETCH r.shipment.vendor " +
                                    "WHERE r.shipment.shipmentId = :shipmentId " +
                                    "ORDER BY r.calculatedAt DESC",
                            RoutePlan.class)
                    .setParameter("shipmentId", shipmentId)
                    .setMaxResults(1)
                    .getResultList();

            return results.isEmpty() ? null : results.get(0);

        } catch (Exception e) {
            AppLogger.error("Failed to find route plan for shipment " + shipmentId, e);
            throw e;
        }
    }
}