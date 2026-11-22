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
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.RoutePlanServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/route-plan/history")
public class RouteHistoryServlet extends HttpServlet {

    @EJB
    private RoutePlanServiceLocal routePlanService;

    @EJB
    private ShipmentServiceLocal shipmentService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String shipmentIdStr = req.getParameter("shipmentId");
        if (shipmentIdStr == null || shipmentIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/route-plan");
            return;
        }

        try {
            Integer shipmentId = Integer.parseInt(shipmentIdStr);
            Shipment shipment = shipmentService.findShipmentById(shipmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

            List<RoutePlan> history = routePlanService.getRoutePlanHistory(shipmentId);

            req.setAttribute("shipment", shipment);
            req.setAttribute("history", history);
            AppLogger.info("Loaded route history for shipment: {}", shipmentId);

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid shipment ID: {}", shipmentIdStr);
            req.setAttribute("error", "Invalid shipment ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Shipment not found: {}", shipmentIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load route history", e);
            req.setAttribute("error", "Failed to load route history");
        }

        req.getRequestDispatcher("/routes/route-history.jsp").forward(req, resp);
    }
}