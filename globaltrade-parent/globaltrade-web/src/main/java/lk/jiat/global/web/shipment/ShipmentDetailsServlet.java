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
import lk.jiat.global.entity.ShipmentItem;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.RoutePlanServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/shipments/details")
public class ShipmentDetailsServlet extends HttpServlet {

    @EJB
    private ShipmentServiceLocal shipmentService;

    @EJB
    private RoutePlanServiceLocal routePlanService;  // ← ADD THIS

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String shipmentIdStr = req.getParameter("shipmentId");
        if (shipmentIdStr == null || shipmentIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/shipments");
            return;
        }

        try {
            Integer shipmentId = Integer.parseInt(shipmentIdStr);
            Shipment shipment = shipmentService.findShipmentById(shipmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

            List<ShipmentItem> shipmentItems = shipmentService.getShipmentItems(shipmentId);

            // ===== FETCH ROUTE PLAN =====
            RoutePlan routePlan = routePlanService.getLatestRoutePlan(shipmentId).orElse(null);

            req.setAttribute("shipment", shipment);
            req.setAttribute("shipmentItems", shipmentItems);
            req.setAttribute("routePlan", routePlan);  // ← ADD THIS

            AppLogger.info("Loaded shipment details for ID: {} with routePlan: {}",
                    shipmentId, routePlan != null ? "found" : "not found");

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid shipment ID: {}", shipmentIdStr);
            req.setAttribute("error", "Invalid shipment ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Shipment not found: {}", shipmentIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load shipment details", e);
            req.setAttribute("error", "Failed to load shipment details");
        }

        req.getRequestDispatcher("/shipments/shipment-details.jsp").forward(req, resp);
    }
}