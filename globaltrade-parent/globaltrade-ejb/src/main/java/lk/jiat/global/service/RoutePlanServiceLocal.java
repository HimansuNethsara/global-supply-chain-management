package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.RoutePlan;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Local
public interface RoutePlanServiceLocal {

    // Create initial route plan from shipment creation
    RoutePlan createInitialRoutePlan(Integer shipmentId, String plannedRoute,
                                     BigDecimal distanceKm, String serviceType)
            throws ResourceNotFoundException, ValidationException;

    Optional<RoutePlan> getLatestRoutePlan(Integer shipmentId);

    List<RoutePlan> getRoutePlanHistory(Integer shipmentId) throws ResourceNotFoundException;

    // Optimize route - creates new route plan or updates existing
    RoutePlan optimizeRoute(Integer shipmentId, Integer routePlanId, String optimizedRoute,
                            BigDecimal distanceKm, String serviceType, boolean createNew)
            throws ResourceNotFoundException, ValidationException;

    Optional<RoutePlan> findRoutePlanById(Integer routePlanId);

    RoutePlan generateSampleRoutePlan(Integer shipmentId) throws ResourceNotFoundException;

    void deleteRoutePlan(Integer routePlanId) throws ResourceNotFoundException;

    // Calculate estimated time based on distance and service type
    BigDecimal calculateEstimatedTime(BigDecimal distanceKm, String serviceType);
}