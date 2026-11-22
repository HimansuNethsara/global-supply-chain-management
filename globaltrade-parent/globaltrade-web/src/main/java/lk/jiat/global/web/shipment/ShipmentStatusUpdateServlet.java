package lk.jiat.global.web.shipment;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.InvalidShipmentStateException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/shipments/status-update")
public class ShipmentStatusUpdateServlet extends HttpServlet {

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
            resp.sendRedirect(req.getContextPath() + "/shipments");
            return;
        }

        try {
            Integer shipmentId = Integer.parseInt(shipmentIdStr);
            Shipment shipment = shipmentService.findShipmentById(shipmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

            req.setAttribute("shipment", shipment);

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid shipment ID: {}", shipmentIdStr);
            req.setAttribute("error", "Invalid shipment ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Shipment not found: {}", shipmentIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load shipment for status update", e);
            req.setAttribute("error", "Failed to load shipment");
        }

        req.getRequestDispatcher("/shipments/shipment-status-update.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");

        String shipmentIdStr = req.getParameter("shipmentId");
        String newStatus = req.getParameter("newStatus");
        String remarks = req.getParameter("remarks");

        try {
            Integer shipmentId = Integer.parseInt(shipmentIdStr);
            shipmentService.updateShipmentStatus(shipmentId, newStatus, userId, remarks);

            session.setAttribute("successNotification", "Shipment status updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/shipments");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid shipment ID");
            resp.sendRedirect(req.getContextPath() + "/shipments/status-update?shipmentId=" + shipmentIdStr);
        } catch (InvalidShipmentStateException e) {
            AppLogger.warning("Invalid shipment state: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/shipments/status-update?shipmentId=" + shipmentIdStr);
        } catch (BusinessException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/shipments/status-update?shipmentId=" + shipmentIdStr);
        } catch (Exception e) {
            AppLogger.error("Failed to update shipment status", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/shipments/status-update?shipmentId=" + shipmentIdStr);
        }
    }
}