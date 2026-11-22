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
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/monitoring/exception/action")
public class ExceptionActionServlet extends HttpServlet {

    @EJB
    private ExceptionLogDAO exceptionLogDAO;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to manage exception logs.");
            resp.sendRedirect(req.getContextPath() + "/monitoring/exception-logs");
            return;
        }

        String action = req.getParameter("action");
        String exceptionIdStr = req.getParameter("exceptionId");

        try {
            if (exceptionIdStr == null || exceptionIdStr.trim().isEmpty()) {
                session.setAttribute("errorNotification", "Exception ID is required");
                resp.sendRedirect(req.getContextPath() + "/monitoring/exception-logs");
                return;
            }

            Integer exceptionId = Integer.parseInt(exceptionIdStr);

            // FIX: Convert Integer to Long for GenericDAO.findById()
            ExceptionLog exceptionLog = exceptionLogDAO.findById(exceptionId.longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Exception not found with ID: " + exceptionId));

            switch (action) {
                case "resolve":
                    exceptionLog.setResolved(true);
                    exceptionLogDAO.update(exceptionLog);
                    session.setAttribute("successNotification", "Exception marked as resolved!");
                    AppLogger.info("Exception {} resolved by {}", exceptionId, session.getAttribute("username"));
                    break;

                case "delete":
                    exceptionLogDAO.delete(exceptionLog);
                    session.setAttribute("successNotification", "Exception log deleted successfully!");
                    AppLogger.info("Exception {} deleted by {}", exceptionId, session.getAttribute("username"));
                    break;

                default:
                    session.setAttribute("errorNotification", "Unknown action: " + action);
                    break;
            }

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid exception ID: {}", exceptionIdStr);
            session.setAttribute("errorNotification", "Invalid exception ID");

        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());

        } catch (Exception e) {
            AppLogger.error("Failed to perform exception action", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
        }

        resp.sendRedirect(req.getContextPath() + "/monitoring/exception-logs");
    }
}