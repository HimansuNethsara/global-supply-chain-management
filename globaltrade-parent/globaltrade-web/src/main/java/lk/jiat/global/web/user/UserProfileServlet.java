package lk.jiat.global.web.user;

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
import java.time.LocalDateTime;

@WebServlet("/user/user-profile")
public class UserProfileServlet extends HttpServlet {

    @EJB
    private UserServiceLocal userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            User user = (User) session.getAttribute("user");

            // Fetch fresh user data from database
            User freshUser = userService.findByUsername(user.getUsername())
                    .orElse(user);

            // Build profile data
            ProfileData profile = new ProfileData();
            profile.setUserId(freshUser.getUserId());
            profile.setUsername(freshUser.getUsername());
            profile.setFullName(freshUser.getFullName());
            profile.setEmail(freshUser.getEmail());
            profile.setStatus(freshUser.getStatus());
            profile.setCreatedAt(freshUser.getCreatedAt());
            profile.setRoleName(freshUser.getRole().getRoleName());

            if (freshUser.getVendor() != null) {
                profile.setVendorName(freshUser.getVendor().getVendorName());
            }
            if (freshUser.getCustomer() != null) {
                profile.setCompanyName(freshUser.getCustomer().getCompanyName());
                profile.setContactPhone(freshUser.getCustomer().getContactPhone());
                profile.setCountry(freshUser.getCustomer().getCountry());
            }

            req.setAttribute("profile", profile);
            AppLogger.info("User profile loaded for: {}", freshUser.getUsername());

        } catch (Exception e) {
            AppLogger.error("Failed to load user profile", e);
            req.setAttribute("error", "Failed to load profile");
        }

        req.getRequestDispatcher("/user/user-profile.jsp").forward(req, resp);
    }

    // Inner class for profile data
    public static class ProfileData {
        private Integer userId;
        private String username;
        private String fullName;
        private String email;
        private String status;
        private LocalDateTime createdAt;
        private String roleName;
        private String vendorName;
        private String companyName;
        private String contactPhone;
        private String country;

        // Getters and setters
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
        public String getVendorName() { return vendorName; }
        public void setVendorName(String vendorName) { this.vendorName = vendorName; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
}