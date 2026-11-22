package lk.jiat.global.web.vendor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.VendorPerformanceEvaluationServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/vendors/performance/recalculate")
public class VendorPerformanceRecalculateServlet extends HttpServlet {

    @EJB
    private VendorPerformanceEvaluationServiceLocal evaluationService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to recalculate vendor ratings.");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");
            return;
        }

        String vendorIdStr = req.getParameter("vendorId");

        try {
            Integer vendorId = Integer.parseInt(vendorIdStr);
            evaluationService.recalculateVendorRating(vendorId);

            session.setAttribute("successNotification", "Vendor rating recalculated successfully!");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance?vendorId=" + vendorId);

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid vendor ID: {}", vendorIdStr);
            session.setAttribute("errorNotification", "Invalid vendor ID");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Vendor not found: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (Exception e) {
            AppLogger.error("Failed to recalculate vendor rating", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");
        }
    }
}