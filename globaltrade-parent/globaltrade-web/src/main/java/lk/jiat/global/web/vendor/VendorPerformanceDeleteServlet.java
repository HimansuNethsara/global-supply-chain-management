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

@WebServlet("/vendors/performance/delete")
public class VendorPerformanceDeleteServlet extends HttpServlet {

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
            session.setAttribute("errorNotification", "You do not have permission to delete vendor evaluations.");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");
            return;
        }

        String evalIdStr = req.getParameter("evalId");

        try {
            Integer evalId = Integer.parseInt(evalIdStr);
            evaluationService.deleteEvaluation(evalId);

            session.setAttribute("successNotification", "Vendor evaluation deleted successfully!");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid evaluation ID: {}", evalIdStr);
            session.setAttribute("errorNotification", "Invalid evaluation ID");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Evaluation not found: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (Exception e) {
            AppLogger.error("Failed to delete vendor evaluation", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");
        }
    }
}