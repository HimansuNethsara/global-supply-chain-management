package lk.jiat.global.web.admin;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.User;
import lk.jiat.global.service.UserServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users-list")
public class AdminUserManagementServlet extends HttpServlet {

    @EJB
    private UserServiceLocal userService;

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
            // Get all users
            List<User> allUsers = userService.findUsersByRole("CUSTOMER");
            allUsers.addAll(userService.findUsersByRole("VENDOR_REP"));
            allUsers.addAll(userService.findUsersByRole("LOGISTICS_COORDINATOR"));
            allUsers.addAll(userService.findUsersByRole("WAREHOUSE_MANAGER"));
            allUsers.addAll(userService.findUsersByRole("CUSTOMS_AGENT"));

            // Get pending users (CUSTOMS_AGENT with PENDING_APPROVAL)
            List<User> pendingUsers = userService.findUsersByRoleAndStatus("CUSTOMS_AGENT", "PENDING_APPROVAL");

            req.setAttribute("allUsers", allUsers);
            req.setAttribute("pendingUsers", pendingUsers);
            req.setAttribute("pendingCount", pendingUsers.size());
            req.setAttribute("totalUsers", allUsers.size());

            AppLogger.info("Loaded user management page: {} total users, {} pending",
                    allUsers.size(), pendingUsers.size());

        } catch (Exception e) {
            AppLogger.error("Failed to load user management", e);
            req.setAttribute("error", "Failed to load users");
        }

        req.getRequestDispatcher("/admin/users-list.jsp").forward(req, resp);
    }
}