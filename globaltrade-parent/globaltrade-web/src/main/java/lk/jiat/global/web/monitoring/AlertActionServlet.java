package lk.jiat.global.web.monitoring;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.AlertServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/monitoring/alert/action")
public class AlertActionServlet extends HttpServlet {

    @EJB
    private AlertServiceLocal alertService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to manage alerts.");
            resp.sendRedirect(req.getContextPath() + "/monitoring/alerts");
            return;
        }

        String action = req.getParameter("action");
        String alertIdStr = req.getParameter("alertId");

        try {
            if (alertIdStr == null || alertIdStr.trim().isEmpty()) {
                if ("resolveAll".equals(action)) {
                    int count = alertService.resolveAllAlerts();
                    session.setAttribute("successNotification", count + " alerts resolved successfully!");
                    resp.sendRedirect(req.getContextPath() + "/monitoring/alerts");
                    return;
                }
                session.setAttribute("errorNotification", "Alert ID is required");
                resp.sendRedirect(req.getContextPath() + "/monitoring/alerts");
                return;
            }

            Integer alertId = Integer.parseInt(alertIdStr);

            switch (action) {
                case "resolve":
                    alertService.resolveAlert(alertId);
                    session.setAttribute("successNotification", "Alert resolved successfully!");
                    break;

                case "delete":
                    alertService.deleteAlert(alertId);
                    session.setAttribute("successNotification", "Alert deleted successfully!");
                    break;

                default:
                    session.setAttribute("errorNotification", "Unknown action: " + action);
                    break;
            }

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid alert ID: {}", alertIdStr);
            session.setAttribute("errorNotification", "Invalid alert ID");

        } catch (ValidationException e) {
            session.setAttribute("errorNotification", e.getMessage());

        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());

        } catch (Exception e) {
            AppLogger.error("Failed to perform alert action", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
        }

        resp.sendRedirect(req.getContextPath() + "/monitoring/alerts");
    }
}