package lk.jiat.global.web.route;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.RoutePlan;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.service.RoutePlanServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/route-plan")
public class RoutePlanServlet extends HttpServlet {

    @EJB
    private ShipmentServiceLocal shipmentService;

    @EJB
    private RoutePlanServiceLocal routePlanService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // Load all shipments for dropdown
            List<Shipment> shipments = shipmentService.findAllShipments();
            req.setAttribute("shipments", shipments);
            AppLogger.info("Loaded {} shipments for route planning", shipments.size());

            // Check if a specific shipment is requested
            String shipmentIdStr = req.getParameter("shipmentId");
            if (shipmentIdStr != null && !shipmentIdStr.trim().isEmpty()) {
                try {
                    Integer shipmentId = Integer.parseInt(shipmentIdStr);

                    // Get the shipment
                    Shipment shipment = shipmentService.findShipmentById(shipmentId).orElse(null);
                    req.setAttribute("shipment", shipment);

                    // Get the latest route plan for this shipment
                    RoutePlan routePlan = routePlanService.getLatestRoutePlan(shipmentId).orElse(null);
                    if (routePlan != null) {
                        // Force initialization of lazy properties
                        routePlan.getShipment().getTrackingNumber();
                        req.setAttribute("routePlan", routePlan);
                        AppLogger.info("Loaded route plan for shipment: {}", shipmentId);
                    } else {
                        // Generate a sample route plan if none exists
                        RoutePlan newPlan = routePlanService.generateSampleRoutePlan(shipmentId);
                        req.setAttribute("routePlan", newPlan);
                        AppLogger.info("Generated sample route plan for shipment: {}", shipmentId);
                    }
                } catch (NumberFormatException e) {
                    AppLogger.warning("Invalid shipment ID: {}", shipmentIdStr);
                } catch (Exception e) {
                    AppLogger.error("Failed to load route plan", e);
                }
            }

        } catch (Exception e) {
            AppLogger.error("Failed to load data for route planning", e);
            req.setAttribute("error", "Failed to load data");
        }

        req.getRequestDispatcher("/routes/route-plan.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String shipmentIdStr = req.getParameter("shipmentId");

        if (shipmentIdStr == null || shipmentIdStr.trim().isEmpty()) {
            session.setAttribute("errorNotification", "Shipment ID is required");
            resp.sendRedirect(req.getContextPath() + "/route-plan");
            return;
        }

        try {
            Integer shipmentId = Integer.parseInt(shipmentIdStr);

            if ("view".equals(action)) {
                resp.sendRedirect(req.getContextPath() + "/route-plan?shipmentId=" + shipmentId);
                return;
            }

            // In RoutePlanServlet.doPost() - optimize action
            if ("optimize".equals(action)) {
                String optimizedRoute = req.getParameter("optimizedRoute");
                String distanceKmStr = req.getParameter("distanceKm");
                String serviceType = req.getParameter("serviceType");
                String routePlanIdStr = req.getParameter("routePlanId");

                // Validate required fields
                if (optimizedRoute == null || optimizedRoute.trim().isEmpty()) {
                    session.setAttribute("errorNotification", "Optimized route description is required");
                    resp.sendRedirect(req.getContextPath() + "/route-plan?shipmentId=" + shipmentId);
                    return;
                }

                // Parse distance
                BigDecimal distanceKm = null;
                if (distanceKmStr != null && !distanceKmStr.trim().isEmpty()) {
                    try {
                        distanceKm = new BigDecimal(distanceKmStr.trim());
                        if (distanceKm.compareTo(BigDecimal.ZERO) <= 0) {
                            session.setAttribute("errorNotification", "Distance must be greater than 0");
                            resp.sendRedirect(req.getContextPath() + "/route-plan?shipmentId=" + shipmentId);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        session.setAttribute("errorNotification", "Invalid distance format");
                        resp.sendRedirect(req.getContextPath() + "/route-plan?shipmentId=" + shipmentId);
                        return;
                    }
                } else {
                    session.setAttribute("errorNotification", "Distance is required");
                    resp.sendRedirect(req.getContextPath() + "/route-plan?shipmentId=" + shipmentId);
                    return;
                }

                // If serviceType is not provided or is "Standard", try to get it from carrier
                if (serviceType == null || serviceType.trim().isEmpty() || "Standard".equals(serviceType.trim())) {
                    Shipment shipment = shipmentService.findShipmentById(shipmentId).orElse(null);
                    if (shipment != null && shipment.getCarrier() != null) {
                        String carrierServiceType = shipment.getCarrier().getServiceType();
                        if (carrierServiceType != null && !carrierServiceType.trim().isEmpty()) {
                            serviceType = carrierServiceType.trim();
                            AppLogger.info("Using carrier service type: {}", serviceType);
                        } else {
                            serviceType = "Standard";
                        }
                    } else {
                        serviceType = "Standard";
                    }
                }

                Integer routePlanId = null;
                if (routePlanIdStr != null && !routePlanIdStr.trim().isEmpty()) {
                    try {
                        routePlanId = Integer.parseInt(routePlanIdStr);
                    } catch (NumberFormatException e) {
                        AppLogger.warning("Invalid route plan ID: {}", routePlanIdStr);
                    }
                }

                AppLogger.info("Optimizing route for shipment {}: route='{}', distance={}km, serviceType={}",
                        shipmentId, optimizedRoute, distanceKm, serviceType);

                // Create new route plan
                RoutePlan optimized = routePlanService.optimizeRoute(
                        shipmentId,
                        routePlanId,
                        optimizedRoute.trim(),
                        distanceKm,
                        serviceType,
                        true
                );

                session.setAttribute("successNotification", "Route optimized successfully! Estimated delivery date updated.");
                resp.sendRedirect(req.getContextPath() + "/route-plan?shipmentId=" + shipmentId);
                return;
            }

            session.setAttribute("errorNotification", "Unknown action: " + action);
            resp.sendRedirect(req.getContextPath() + "/route-plan");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid shipment ID");
            resp.sendRedirect(req.getContextPath() + "/route-plan");
        } catch (Exception e) {
            AppLogger.error("Failed to process route action", e);
            session.setAttribute("errorNotification", "An unexpected error occurred: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/route-plan");
        }
    }
}