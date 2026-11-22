package lk.jiat.global.web.monitoring;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.SystemTimer;
import lk.jiat.global.service.SystemTimerServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/monitoring/timers")
public class TimerListServlet extends HttpServlet {

    @EJB
    private SystemTimerServiceLocal systemTimerService;

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
            List<SystemTimer> timers = systemTimerService.getAllTimers();
            req.setAttribute("timers", timers);

            // Calculate stats
            long activeCount = timers.stream().filter(t -> "ACTIVE".equals(t.getStatus())).count();
            long pausedCount = timers.stream().filter(t -> "PAUSED".equals(t.getStatus())).count();
            long failedCount = timers.stream().filter(t -> "FAILED".equals(t.getStatus())).count();

            req.setAttribute("activeCount", activeCount);
            req.setAttribute("pausedCount", pausedCount);
            req.setAttribute("failedCount", failedCount);

            AppLogger.info("Loaded {} timers", timers.size());

        } catch (Exception e) {
            AppLogger.error("Failed to load timers", e);
            req.setAttribute("error", "Failed to load timers");
        }

        req.getRequestDispatcher("/monitoring/system-timers.jsp").forward(req, resp);
    }
}