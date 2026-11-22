package lk.jiat.global.web.shipment;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.RoutePlan;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.service.RoutePlanServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.service.VendorServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/shipments")
public class ShipmentListServlet extends HttpServlet {

    @EJB
    private ShipmentServiceLocal shipmentService;

    @EJB
    private VendorServiceLocal vendorService;

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
            List<Shipment> shipments = shipmentService.findAllShipments();
            List<Vendor> vendors = vendorService.findAllVendors();

            // Fetch route plan for each shipment
            Map<Integer, RoutePlan> routePlanMap = new HashMap<>();
            for (Shipment shipment : shipments) {
                try {
                    RoutePlan routePlan = routePlanService
                            .getLatestRoutePlan(shipment.getShipmentId())
                            .orElse(null);

                    if (routePlan != null) {
                        routePlanMap.put(shipment.getShipmentId(), routePlan);

                        AppLogger.info(
                                "Route plan loaded for shipment " +
                                        shipment.getShipmentId() +
                                        ": planned=" + routePlan.getPlannedRoute() +
                                        ", optimized=" + routePlan.getOptimizedRoute()
                        );
                    } else {
                        AppLogger.info(
                                "No route plan exists for shipment " +
                                        shipment.getShipmentId()
                        );
                    }

                } catch (Exception e) {
                    AppLogger.error(
                            "Failed to load route plan for shipment " +
                                    shipment.getShipmentId(),
                            e
                    );
                }
            }

            req.setAttribute("shipments", shipments);
            req.setAttribute("vendors", vendors);
            req.setAttribute("routePlanMap", routePlanMap);
            AppLogger.info("Loaded {} shipments, {} vendors, and {} route plans",
                    shipments.size(), vendors.size(), routePlanMap.size());

        } catch (Exception e) {
            AppLogger.error("Failed to load shipments", e);
            req.setAttribute("error", "Failed to load shipments: " + e.getMessage());
        }

        req.getRequestDispatcher("/shipments/shipments-list.jsp").forward(req, resp);
    }
}