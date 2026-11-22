package lk.jiat.global.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.UserServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @EJB
    private UserServiceLocal userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) != null && req.getSession().getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            // Load vendors for dropdown
            List<Vendor> vendors = userService.findAllVendors();
            req.setAttribute("vendors", vendors);
            AppLogger.info("Loaded {} vendors for registration page", vendors.size());
        } catch (Exception e) {
            AppLogger.error("Failed to load vendors for registration", e);
        }

        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Clear any previous notification attributes
        req.getSession().removeAttribute("errorNotification");
        req.getSession().removeAttribute("successNotification");
        req.getSession().removeAttribute("redirectAfterSuccess");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String email = req.getParameter("email");
        String fullName = req.getParameter("fullName");
        String role = req.getParameter("role");
        String vendorIdStr = req.getParameter("vendorId");
        String nicNumber = req.getParameter("nicNumber");

        try {
            // Validate required fields
            if (username == null || username.trim().isEmpty() ||
                    password == null || password.trim().isEmpty() ||
                    confirmPassword == null || confirmPassword.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    fullName == null || fullName.trim().isEmpty() ||
                    role == null || role.trim().isEmpty()) {
                throw new ValidationException("All fields are required");
            }

            // Validate password confirmation
            if (!password.equals(confirmPassword)) {
                throw new ValidationException("Passwords do not match");
            }

            // Route to appropriate registration method based on role
            switch (role) {
                case "CUSTOMER":
                case "VENDOR_REP":
                    Integer vendorId = null;
                    if ("VENDOR_REP".equals(role)) {
                        try {
                            vendorId = Integer.parseInt(vendorIdStr);
                        } catch (NumberFormatException e) {
                            throw new ValidationException("Please select a valid vendor");
                        }
                    }
                    userService.register(
                            username.trim(),
                            password,
                            email.trim(),
                            fullName.trim(),
                            role,
                            vendorId
                    );
                    AppLogger.info("New " + role + " registered: " + username);
                    break;

                case "LOGISTICS_COORDINATOR":
                case "WAREHOUSE_MANAGER":
                    if (nicNumber == null || nicNumber.trim().isEmpty()) {
                        throw new ValidationException("NIC number is required for staff registration");
                    }
                    userService.registerStaffWithNic(
                            username.trim(),
                            password,
                            email.trim(),
                            fullName.trim(),
                            role,
                            nicNumber.trim()
                    );
                    AppLogger.info("New staff " + role + " registered with NIC: " + username);
                    break;

                case "CUSTOMS_AGENT":
                    userService.registerCustomsAgent(
                            username.trim(),
                            password,
                            email.trim(),
                            fullName.trim(),
                            role
                    );
                    AppLogger.info("New " + role + " registered (pending approval): " + username);
                    break;

                default:
                    throw new ValidationException("Invalid role selected");
            }

            // Show success on register page, then redirect to login
            req.getSession().setAttribute("successNotification", "Registration successful! Please login to continue.");
            req.getSession().setAttribute("redirectAfterSuccess", "/login");
            resp.sendRedirect(req.getContextPath() + "/register");

        } catch (DuplicateResourceException e) {
            String errorMsg = e.getMessage();
            AppLogger.warning("Registration failed - duplicate: " + errorMsg);
            req.getSession().setAttribute("errorNotification", errorMsg);
            resp.sendRedirect(req.getContextPath() + "/register");

        } catch (BusinessException e) {
            String errorMsg = e.getMessage();
            AppLogger.warning("Registration failed for " + username + ": " + errorMsg);
            req.getSession().setAttribute("errorNotification", errorMsg);
            resp.sendRedirect(req.getContextPath() + "/register");

        } catch (Exception e) {
            AppLogger.error("Unexpected error during registration for " + username, e);
            req.getSession().setAttribute("errorNotification", "An unexpected error occurred. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/register");
        }
    }
}