package lk.jiat.global.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.InvalidCredentialsException;
import lk.jiat.global.service.UserServiceLocal;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.UserContext;  // ← ADD THIS IMPORT

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB
    private UserServiceLocal userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        // Clear any stale UserContext
        UserContext.clear();

        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Validate input
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            AppLogger.warning("Login attempt with empty credentials");
            req.getSession().setAttribute("errorNotification", "Username and password are required");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // Authenticate
            User user = userService.authenticate(username.trim(), password);

            // Check if user is active
            if (!"ACTIVE".equals(user.getStatus())) {
                AppLogger.warning("Login attempt by inactive user: {} (status: {})", username, user.getStatus());
                req.getSession().setAttribute("errorNotification", "Your account is " + user.getStatus().toLowerCase() + ". Please contact support.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            // Create HTTP session
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().getRoleName());
            session.setAttribute("fullName", user.getFullName());

            // If vendor rep, store vendor info
            if (user.getVendor() != null) {
                session.setAttribute("vendorId", user.getVendor().getVendorId());
                session.setAttribute("vendorName", user.getVendor().getVendorName());
            }

            // ===== SET USER CONTEXT FOR INTERCEPTORS =====
            UserContext.setCurrentUser(user.getUserId(), user.getUsername());

            AppLogger.info("User logged in: {} (role: {})", username, user.getRole().getRoleName());

            // Set success notification and redirect to dashboard
            session.setAttribute("successNotification", "Welcome back, " + user.getFullName() + "!");
            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (InvalidCredentialsException e) {
            AppLogger.warning("Failed login attempt for username: {}", username);
            req.getSession().setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/login");
            // Clear UserContext on failed login
            UserContext.clear();
        } catch (Exception e) {
            AppLogger.error("Unexpected error during login for " + username, e);
            req.getSession().setAttribute("errorNotification", "An unexpected error occurred. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/login");
            UserContext.clear();
        }
    }
}