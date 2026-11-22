package lk.jiat.global.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.RoutePlanDAO;
import lk.jiat.global.dao.ShipmentDAO;
import lk.jiat.global.entity.RoutePlan;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.ServiceType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
@ExceptionLogged
public class RoutePlanService implements RoutePlanServiceLocal {

    @Inject
    private RoutePlanDAO routePlanDAO;

    @Inject
    private ShipmentDAO shipmentDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RoutePlan createInitialRoutePlan(Integer shipmentId, String plannedRoute,
                                            BigDecimal distanceKm, String serviceType)
            throws ResourceNotFoundException, ValidationException {

        if (shipmentId == null) {
            throw new ValidationException("Shipment ID is required");
        }
        if (plannedRoute == null || plannedRoute.trim().isEmpty()) {
            throw new ValidationException("Planned route is required");
        }

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        // Calculate estimated time based on distance and service type
        BigDecimal estimatedTime = calculateEstimatedTime(distanceKm, serviceType);

        RoutePlan routePlan = new RoutePlan();
        routePlan.setShipment(shipment);
        routePlan.setPlannedRoute(plannedRoute.trim());
        routePlan.setDistanceKm(distanceKm != null ? distanceKm.setScale(2, RoundingMode.HALF_UP) : null);
        routePlan.setEstimatedTimeHours(estimatedTime);
        routePlan.setCalculatedAt(LocalDateTime.now());
        routePlan.setOptimizedRoute(null);

        routePlanDAO.create(routePlan);

        // Update shipment estimated delivery date
        updateShipmentEstimatedDeliveryDate(shipment, distanceKm, serviceType);

        return routePlan;
    }

    @Override
    public List<RoutePlan> getRoutePlanHistory(Integer shipmentId) throws ResourceNotFoundException {
        if (!shipmentDAO.findById(shipmentId).isPresent()) {
            throw new ResourceNotFoundException("Shipment not found with ID: " + shipmentId);
        }
        return routePlanDAO.findByShipmentId(shipmentId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RoutePlan optimizeRoute(Integer shipmentId, Integer routePlanId, String optimizedRoute,
                                   BigDecimal distanceKm, String serviceType, boolean createNew)
            throws ResourceNotFoundException, ValidationException {

        if (shipmentId == null) {
            throw new ValidationException("Shipment ID is required");
        }

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        // Check if shipment can be optimized
        String status = shipment.getStatus();
        if ("DELIVERED".equals(status) || "CANCELLED".equals(status)) {
            throw new ValidationException("Cannot optimize route for " + status.toLowerCase() + " shipments");
        }

        RoutePlan sourcePlan;
        if (routePlanId != null) {
            sourcePlan = routePlanDAO.findById(routePlanId)
                    .orElseThrow(() -> new ResourceNotFoundException("Route plan not found with ID: " + routePlanId));
            if (!sourcePlan.getShipment().getShipmentId().equals(shipmentId)) {
                throw new ValidationException("Route plan does not belong to this shipment");
            }
        } else {
            sourcePlan = routePlanDAO.findLatestByShipmentId(shipmentId);
            if (sourcePlan == null) {
                throw new ResourceNotFoundException("No route plan found for shipment: " + shipmentId);
            }
        }

        // Calculate estimated time
        BigDecimal estimatedTime = calculateEstimatedTime(distanceKm, serviceType);

        RoutePlan result;
        if (createNew) {
            RoutePlan newPlan = new RoutePlan();
            newPlan.setShipment(shipment);
            newPlan.setPlannedRoute(sourcePlan.getPlannedRoute());
            newPlan.setOptimizedRoute(optimizedRoute != null ? optimizedRoute.trim() : null);
            newPlan.setDistanceKm(distanceKm != null ? distanceKm.setScale(2, RoundingMode.HALF_UP) : null);
            newPlan.setEstimatedTimeHours(estimatedTime);
            newPlan.setCalculatedAt(LocalDateTime.now());
            routePlanDAO.create(newPlan);
            result = newPlan;
        } else {
            if (optimizedRoute != null && !optimizedRoute.trim().isEmpty()) {
                sourcePlan.setOptimizedRoute(optimizedRoute.trim());
            }
            if (distanceKm != null) {
                sourcePlan.setDistanceKm(distanceKm.setScale(2, RoundingMode.HALF_UP));
            }
            if (estimatedTime != null) {
                sourcePlan.setEstimatedTimeHours(estimatedTime);
            }
            result = routePlanDAO.update(sourcePlan);
        }

        updateShipmentEstimatedDeliveryDate(shipment, distanceKm, serviceType);

        AppLogger.info("Route optimized for shipment {}: new distance={}km, est time={}hrs",
                shipmentId, distanceKm, estimatedTime);

        return result;
    }

    @Override
    public Optional<RoutePlan> findRoutePlanById(Integer routePlanId) {
        return routePlanDAO.findById(routePlanId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteRoutePlan(Integer routePlanId) throws ResourceNotFoundException {
        RoutePlan routePlan = routePlanDAO.findById(routePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Route plan not found with ID: " + routePlanId));
        routePlanDAO.delete(routePlan);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RoutePlan generateSampleRoutePlan(Integer shipmentId) throws ResourceNotFoundException {
        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        String origin = shipment.getOriginWarehouse().getLocation();
        String destination = shipment.getDestination();
        String plannedRoute = "Route from " + origin + " to " + destination + " via Highway A1";

        int distance = 100 + (int)(Math.random() * 900);
        BigDecimal distanceKm = BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);

        String serviceType = null;
        if (shipment.getCarrier() != null) {
            serviceType = shipment.getCarrier().getServiceType();
        }
        BigDecimal estimatedTime = calculateEstimatedTime(distanceKm, serviceType);

        RoutePlan routePlan = new RoutePlan();
        routePlan.setShipment(shipment);
        routePlan.setPlannedRoute(plannedRoute);
        routePlan.setDistanceKm(distanceKm);
        routePlan.setEstimatedTimeHours(estimatedTime);
        routePlan.setCalculatedAt(LocalDateTime.now());
        routePlan.setOptimizedRoute(null);

        routePlanDAO.create(routePlan);

        updateShipmentEstimatedDeliveryDate(shipment, distanceKm, serviceType);

        return routePlan;
    }

    @Override
    public Optional<RoutePlan> getLatestRoutePlan(Integer shipmentId) {
        try {
            RoutePlan routePlan = routePlanDAO.findLatestByShipmentId(shipmentId);
            if (routePlan != null) {
                // Force initialization of lazy properties
                routePlan.getShipment().getTrackingNumber();
                routePlan.getShipment().getOriginWarehouse().getName();
                routePlan.getShipment().getVendor().getVendorName();
            }
            return Optional.ofNullable(routePlan);
        } catch (Exception e) {
            AppLogger.warning("Failed to get latest route plan for shipment {}: {}", shipmentId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public BigDecimal calculateEstimatedTime(BigDecimal distanceKm, String serviceType) {
        if (distanceKm == null) {
            return null;
        }

        double speed = 50.0; // Default speed

        if (serviceType != null && !serviceType.isEmpty()) {
            ServiceType type = ServiceType.fromDisplayName(serviceType);
            if (type != null) {
                speed = type.getAvgSpeedKmh();
                if (speed <= 0) {
                    speed = 50.0;
                }
            }
        }

        double hours = distanceKm.doubleValue() / speed;
        return BigDecimal.valueOf(hours).setScale(2, RoundingMode.HALF_UP);
    }

    private void updateShipmentEstimatedDeliveryDate(Shipment shipment, BigDecimal distanceKm, String serviceType) {
        if (shipment == null || distanceKm == null) {
            return;
        }

        BigDecimal estimatedTimeHours = calculateEstimatedTime(distanceKm, serviceType);
        if (estimatedTimeHours == null) {
            return;
        }

        LocalDateTime scheduledDate = shipment.getScheduledDate();
        if (scheduledDate == null) {
            AppLogger.warning("Shipment {} has no scheduled date, cannot update estimated delivery date",
                    shipment.getShipmentId());
            return;
        }

        double hours = estimatedTimeHours.doubleValue();
        long days = (long) Math.ceil(hours / 24);
        if (days < 1) {
            days = 1;
        }

        LocalDateTime estimatedDeliveryDate = scheduledDate.plusDays(days);
        shipment.setEstimatedDeliveryDate(estimatedDeliveryDate);
        shipmentDAO.update(shipment);

        AppLogger.info("Updated estimated delivery date for shipment {}: scheduled={}, distance={}km, serviceType={}, hours={}, days={}, new estimated={}",
                shipment.getShipmentId(), scheduledDate, distanceKm, serviceType, hours, days, estimatedDeliveryDate);
    }
}