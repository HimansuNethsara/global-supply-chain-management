package lk.jiat.global.web.vendor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.entity.VendorPerformanceEvaluation;
import lk.jiat.global.service.VendorPerformanceEvaluationServiceLocal;
import lk.jiat.global.service.VendorServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/vendors/performance")
public class VendorPerformanceListServlet extends HttpServlet {

    @EJB
    private VendorPerformanceEvaluationServiceLocal evaluationService;

    @EJB
    private VendorServiceLocal vendorService;

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
            // Get all vendors for dropdown
            List<Vendor> vendors = vendorService.findAllVendors();
            req.setAttribute("vendors", vendors);

            // Check if a vendor is selected
            String vendorIdStr = req.getParameter("vendorId");
            if (vendorIdStr != null && !vendorIdStr.trim().isEmpty()) {
                try {
                    Integer vendorId = Integer.parseInt(vendorIdStr);

                    // Get vendor details
                    Vendor vendor = vendorService.findVendorById(vendorId).orElse(null);
                    req.setAttribute("vendor", vendor);

                    // Get evaluations
                    List<VendorPerformanceEvaluation> evaluations = evaluationService.getEvaluationsForVendor(vendorId);
                    req.setAttribute("evaluations", evaluations);

                    // Get average rating
                    BigDecimal avgRating = evaluationService.getAverageRating(vendorId);
                    req.setAttribute("avgRating", avgRating);

                    AppLogger.info("Loaded performance data for vendor: {}", vendorId);

                } catch (NumberFormatException e) {
                    AppLogger.warning("Invalid vendor ID: {}", vendorIdStr);
                }
            }

        } catch (Exception e) {
            AppLogger.error("Failed to load vendor performance page", e);
            req.setAttribute("error", "Failed to load vendor performance data");
        }

        req.getRequestDispatcher("/vendors/vendor-performance-evaluation.jsp").forward(req, resp);
    }
}