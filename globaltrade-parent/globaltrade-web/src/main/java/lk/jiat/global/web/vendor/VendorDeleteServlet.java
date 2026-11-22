package lk.jiat.global.web.vendor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.VendorServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/vendors/delete")
public class VendorDeleteServlet extends HttpServlet {

    @EJB
    private VendorServiceLocal vendorService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // ===== AUTHORIZATION CHECK: Only LOGISTICS_COORDINATOR can delete =====
        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            AppLogger.warning("Unauthorized delete attempt by user with role: {}", role);
            session.setAttribute("errorNotification", "You do not have permission to delete vendors.");
            resp.sendRedirect(req.getContextPath() + "/vendors");
            return;
        }

        String vendorIdStr = req.getParameter("vendorId");
        if (vendorIdStr == null || vendorIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/vendors");
            return;
        }

        try {
            Integer vendorId = Integer.parseInt(vendorIdStr);
            vendorService.deleteVendor(vendorId);
            AppLogger.info("Vendor deleted successfully: {}", vendorId);
            session.setAttribute("successNotification", "Vendor deleted successfully!");

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid vendor ID: {}", vendorIdStr);
            session.setAttribute("errorNotification", "Invalid vendor ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Vendor not found: {}", vendorIdStr);
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to delete vendor", e);
            session.setAttribute("errorNotification", "Failed to delete vendor");
        }

        resp.sendRedirect(req.getContextPath() + "/vendors");
    }
}