package lk.jiat.global.web.shipment;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/shipments/delete")
public class ShipmentDeleteServlet extends HttpServlet {

    @EJB
    private ShipmentServiceLocal shipmentService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to delete shipments.");
            resp.sendRedirect(req.getContextPath() + "/shipments");
            return;
        }

        String shipmentIdStr = req.getParameter("shipmentId");
        if (shipmentIdStr == null || shipmentIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/shipments");
            return;
        }

        try {
            Integer shipmentId = Integer.parseInt(shipmentIdStr);
            shipmentService.deleteShipment(shipmentId);
            session.setAttribute("successNotification", "Shipment deleted successfully!");
        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid shipment ID");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to delete shipment", e);
            session.setAttribute("errorNotification", "Failed to delete shipment");
        }

        resp.sendRedirect(req.getContextPath() + "/shipments");
    }
}