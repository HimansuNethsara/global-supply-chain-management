package lk.jiat.global.web.vendor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.VendorServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/vendors/update")
public class VendorUpdateServlet extends HttpServlet {

    @EJB
    private VendorServiceLocal vendorService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to update vendors.");
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
            Integer vendorId = Integer.parseInt(vendorIdStr);
            vendorService.updateVendor(vendorId, vendorName, contactEmail, contactPhone, country, status);
            session.setAttribute("successNotification", "Vendor updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/vendors/details?vendorId=" + vendorId);
        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid vendor ID");
            resp.sendRedirect(req.getContextPath() + "/vendors");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/vendors");
        } catch (ValidationException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/vendors/edit?vendorId=" + vendorIdStr);
        } catch (Exception e) {
            AppLogger.error("Failed to update vendor", e);
            session.setAttribute("errorNotification", "Failed to update vendor");
            resp.sendRedirect(req.getContextPath() + "/vendors/edit?vendorId=" + vendorIdStr);
        }
    }
}