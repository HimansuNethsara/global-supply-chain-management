package lk.jiat.global.web.monitoring;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.dao.ExceptionLogDAO;
import lk.jiat.global.entity.ExceptionLog;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/monitoring/exception-logs")
public class ExceptionLogsServlet extends HttpServlet {

    @EJB
    private ExceptionLogDAO exceptionLogDAO;

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
            List<ExceptionLog> exceptionLogs = exceptionLogDAO.findAll();

            // Sort by occurredAt descending (newest first)
            exceptionLogs.sort((a, b) -> b.getOccurredAt().compareTo(a.getOccurredAt()));

            // Calculate stats
            long unresolvedCount = 0;
            long resolvedCount = 0;
            long appExceptionCount = 0;

            LocalDateTime todayStart = LocalDate.now().atStartOfDay();

            for (ExceptionLog log : exceptionLogs) {
                if (log.getResolved()) {
                    resolvedCount++;
                } else {
                    unresolvedCount++;
                }

                if (log.getExceptionType() != null &&
                        (log.getExceptionType().contains("Application") ||
                                log.getExceptionType().contains("Validation") ||
                                log.getExceptionType().contains("Duplicate") ||
                                log.getExceptionType().contains("Resource") ||
                                log.getExceptionType().contains("InvalidCredentials"))) {
                    appExceptionCount++;
                }
            }

            req.setAttribute("exceptionLogs", exceptionLogs);
            req.setAttribute("unresolvedCount", unresolvedCount);
            req.setAttribute("resolvedCount", resolvedCount);
            req.setAttribute("appExceptionCount", appExceptionCount);

            AppLogger.info("Loaded {} exception logs", exceptionLogs.size());

        } catch (Exception e) {
            AppLogger.error("Failed to load exception logs", e);
            req.setAttribute("error", "Failed to load exception logs");
        }

        req.getRequestDispatcher("/monitoring/exception-logs.jsp").forward(req, resp);
    }
}