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

@WebServlet("/admin/users/approve")
public class AdminUserApproveServlet extends HttpServlet {

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
            session.setAttribute("errorNotification", "You do not have permission to approve users.");
            resp.sendRedirect(req.getContextPath() + "/admin/users-list");
            return;
        }

        String userIdStr = req.getParameter("userId");

        try {
            Integer userId = Integer.parseInt(userIdStr);
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            user.setStatus("ACTIVE");
            userDAO.update(user);

            AppLogger.info("User {} approved by {}", userId, session.getAttribute("username"));
            session.setAttribute("successNotification", "User approved successfully!");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid user ID");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to approve user", e);
            session.setAttribute("errorNotification", "Failed to approve user");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users-list");
    }
}