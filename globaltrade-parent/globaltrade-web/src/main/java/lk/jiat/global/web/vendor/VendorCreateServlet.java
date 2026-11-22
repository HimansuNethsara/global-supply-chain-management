package lk.jiat.global.web.vendor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.exception.VendorSuspendedException;
import lk.jiat.global.service.VendorServiceLocal;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.Country;

import java.io.IOException;

@WebServlet({"/vendors/create", "/vendors/edit"})
public class VendorCreateServlet extends HttpServlet {

    @EJB
    private VendorServiceLocal vendorService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Check if user has permission (LOGISTICS_COORDINATOR only)
        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=LOGISTICS_COORDINATOR");
            return;
        }

        // Check if this is an edit request
        String vendorIdStr = req.getParameter("vendorId");
        if (vendorIdStr != null && !vendorIdStr.trim().isEmpty()) {
            try {
                Integer vendorId = Integer.parseInt(vendorIdStr);
                Vendor vendor = vendorService.findVendorById(vendorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
                req.setAttribute("vendor", vendor);
                req.setAttribute("editMode", true);
                AppLogger.info("Loading vendor for edit: {}", vendorId);
            } catch (NumberFormatException e) {
                AppLogger.warning("Invalid vendor ID: {}", vendorIdStr);
            } catch (ResourceNotFoundException e) {
                AppLogger.warning("Vendor not found: {}", vendorIdStr);
                req.setAttribute("error", e.getMessage());
            }
        } else {
            req.setAttribute("editMode", false);
        }

        // Add country list for dropdown
        req.setAttribute("countryList", Country.getAllDisplayNames());

        req.getRequestDispatcher("/vendors/vendor-create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Check authorization
        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to manage vendors.");
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=LOGISTICS_COORDINATOR");
            return;
        }

        String vendorIdStr = req.getParameter("vendorId");
        String vendorName = req.getParameter("vendorName");
        String contactEmail = req.getParameter("contactEmail");
        String contactPhone = req.getParameter("contactPhone");
        String country = req.getParameter("country");
        String status = req.getParameter("status");

        try {
            if (vendorIdStr != null && !vendorIdStr.trim().isEmpty()) {
                // UPDATE existing vendor
                Integer vendorId = Integer.parseInt(vendorIdStr);
                Vendor vendor = vendorService.updateVendor(
                        vendorId,
                        vendorName,
                        contactEmail,
                        contactPhone,
                        country,
                        status
                );
                AppLogger.info("Vendor updated successfully: {} (ID: {})", vendor.getVendorName(), vendor.getVendorId());
                session.setAttribute("successNotification", "Vendor '" + vendor.getVendorName() + "' updated successfully!");
            } else {
                // CREATE new vendor
                Vendor vendor = vendorService.createVendor(
                        vendorName,
                        contactEmail,
                        contactPhone,
                        country,
                        status
                );
                AppLogger.info("Vendor created successfully: {} (ID: {})", vendor.getVendorName(), vendor.getVendorId());
                session.setAttribute("successNotification", "Vendor '" + vendor.getVendorName() + "' created successfully!");
            }
            resp.sendRedirect(req.getContextPath() + "/vendors");

        } catch (VendorSuspendedException e) {
            AppLogger.warning("Vendor suspended error: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/vendors/create");
        } catch (ValidationException | DuplicateResourceException e) {
            AppLogger.warning("Error processing vendor: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/vendors/create");
        } catch (Exception e) {
            AppLogger.error("Unexpected error processing vendor", e);
            session.setAttribute("errorNotification", "An unexpected error occurred. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/vendors/create");
        }
    }
}