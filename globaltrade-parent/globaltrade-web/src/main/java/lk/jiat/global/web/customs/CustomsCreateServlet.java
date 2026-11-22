package lk.jiat.global.web.customs;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.CustomsServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.service.UserServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/customs/create")
public class CustomsCreateServlet extends HttpServlet {

    @EJB
    private CustomsServiceLocal customsService;

    @EJB
    private ShipmentServiceLocal shipmentService;

    @EJB
    private UserServiceLocal userService;  // ← ADD THIS

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=LOGISTICS_COORDINATOR");
            return;
        }

        try {
            // Load shipments for dropdown
            List<Shipment> shipments = shipmentService.findAllShipments();
            req.setAttribute("shipments", shipments);

            // ===== LOAD CUSTOMS AGENTS =====
            // Get all users with role CUSTOMS_AGENT and status ACTIVE
            List<User> customsAgents = userService.findUsersByRoleAndStatus("CUSTOMS_AGENT", "ACTIVE");
            req.setAttribute("customsAgents", customsAgents);

            AppLogger.info("Loaded {} shipments and {} customs agents", shipments.size(), customsAgents.size());

        } catch (Exception e) {
            AppLogger.error("Failed to load data", e);
            req.setAttribute("error", "Failed to load data: " + e.getMessage());
        }

        req.getRequestDispatcher("/customs/customs-document-create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to create customs documents.");
            resp.sendRedirect(req.getContextPath() + "/customs");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        String shipmentIdStr = req.getParameter("shipmentId");
        String documentType = req.getParameter("documentType");

        try {
            Integer shipmentId = Integer.parseInt(shipmentIdStr);
            customsService.createDocument(shipmentId, documentType, userId);
            session.setAttribute("successNotification", "Customs document created successfully!");
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid shipment ID");
            resp.sendRedirect(req.getContextPath() + "/customs/create");

        } catch (ValidationException | DuplicateResourceException | ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customs/create");

        } catch (Exception e) {
            AppLogger.error("Failed to create customs document", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/customs/create");
        }
    }
}