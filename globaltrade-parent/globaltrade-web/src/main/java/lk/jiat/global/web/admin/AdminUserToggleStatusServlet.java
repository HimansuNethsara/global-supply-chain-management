package lk.jiat.global.web.admin;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.dao.UserDAO;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/admin/users/toggle-status")
public class AdminUserToggleStatusServlet extends HttpServlet {

    @EJB
    private UserDAO userDAO;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to manage users.");
            resp.sendRedirect(req.getContextPath() + "/admin/users-list");
            return;
        }

        String userIdStr = req.getParameter("userId");

        try {
            Integer userId = Integer.parseInt(userIdStr);
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Toggle status
            if ("DISABLED".equals(user.getStatus())) {
                user.setStatus("ACTIVE");
                AppLogger.info("User {} enabled by {}", userId, session.getAttribute("username"));
                session.setAttribute("successNotification", "User enabled successfully!");
            } else if ("ACTIVE".equals(user.getStatus())) {
                user.setStatus("SUSPENDED");
                AppLogger.info("User {} suspended by {}", userId, session.getAttribute("username"));
                session.setAttribute("successNotification", "User suspended successfully!");
            } else if ("SUSPENDED".equals(user.getStatus())) {
                user.setStatus("ACTIVE");
                AppLogger.info("User {} unsuspended by {}", userId, session.getAttribute("username"));
                session.setAttribute("successNotification", "User unsuspended successfully!");
            } else {
                session.setAttribute("errorNotification", "Cannot toggle status for this user");
                resp.sendRedirect(req.getContextPath() + "/admin/users-list");
                return;
            }

            userDAO.update(user);

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid user ID");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to toggle user status", e);
            session.setAttribute("errorNotification", "Failed to toggle user status");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users-list");
    }
}