package lk.jiat.global.web.vendor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.VendorServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/vendors/details")
public class VendorDetailsServlet extends HttpServlet {

    @EJB
    private VendorServiceLocal vendorService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String vendorIdStr = req.getParameter("vendorId");
        if (vendorIdStr == null || vendorIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/vendors");
            return;
        }

        try {
            Integer vendorId = Integer.parseInt(vendorIdStr);
            Vendor vendor = vendorService.findVendorById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

            req.setAttribute("vendor", vendor);
            AppLogger.info("Loaded vendor details for ID: {}", vendorId);

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid vendor ID: {}", vendorIdStr);
            req.setAttribute("error", "Invalid vendor ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Vendor not found: {}", vendorIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load vendor details", e);
            req.setAttribute("error", "Failed to load vendor details");
        }

        req.getRequestDispatcher("/vendors/vendor-details.jsp").forward(req, resp);
    }
}