package lk.jiat.global.web.monitoring;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Alert;
import lk.jiat.global.service.AlertServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/monitoring/alerts")
public class AlertListServlet extends HttpServlet {

    @EJB
    private AlertServiceLocal alertService;

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
            List<Alert> alerts = alertService.getActiveAlerts();


            req.setAttribute("alerts", alerts);
            req.setAttribute("activeCount", alertService.getActiveAlertCount());

            // Calculate counts by severity
            long criticalCount = alertService.getAlertCountBySeverity("CRITICAL");
            long highCount = alertService.getAlertCountBySeverity("HIGH");
            long mediumCount = alertService.getAlertCountBySeverity("MEDIUM");
            long resolvedCount = alerts.stream().filter(Alert::getResolved).count();

            req.setAttribute("criticalCount", criticalCount);
            req.setAttribute("highCount", highCount);
            req.setAttribute("mediumCount", mediumCount);
            req.setAttribute("resolvedCount", resolvedCount);

            AppLogger.info("Loaded {} alerts", alerts.size());

        } catch (Exception e) {
            AppLogger.error("Failed to load alerts", e);
            req.setAttribute("error", "Failed to load alerts");
        }

        req.getRequestDispatcher("/monitoring/alerts.jsp").forward(req, resp);
    }
}