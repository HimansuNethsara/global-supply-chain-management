package lk.jiat.global.web.monitoring;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.dao.AuditLogDAO;
import lk.jiat.global.entity.AuditLog;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/monitoring/audit-logs")
public class AuditLogsServlet extends HttpServlet {

    @EJB
    private AuditLogDAO auditLogDAO;

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
            // Get all audit logs
            List<AuditLog> auditLogs = auditLogDAO.findAll();

            // Sort by loggedAt descending (newest first)
            auditLogs.sort((a, b) -> b.getLoggedAt().compareTo(a.getLoggedAt()));

            // Calculate stats
            long todayCount = 0;
            long userActionCount = 0;
            long systemActionCount = 0;

            LocalDateTime todayStart = LocalDate.now().atStartOfDay();

            for (AuditLog log : auditLogs) {
                if (log.getLoggedAt().isAfter(todayStart)) {
                    todayCount++;
                }
                if (log.getUser() != null) {
                    userActionCount++;
                } else {
                    systemActionCount++;
                }
            }

            req.setAttribute("auditLogs", auditLogs);
            req.setAttribute("todayCount", todayCount);
            req.setAttribute("userActionCount", userActionCount);
            req.setAttribute("systemActionCount", systemActionCount);

            AppLogger.info("Loaded {} audit logs", auditLogs.size());

        } catch (Exception e) {
            AppLogger.error("Failed to load audit logs", e);
            req.setAttribute("error", "Failed to load audit logs");
        }

        req.getRequestDispatcher("/monitoring/audit-logs.jsp").forward(req, resp);
    }
}