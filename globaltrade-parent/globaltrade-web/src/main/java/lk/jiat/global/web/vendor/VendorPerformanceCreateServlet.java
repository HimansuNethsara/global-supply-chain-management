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
import lk.jiat.global.service.VendorPerformanceEvaluationServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@WebServlet("/vendors/performance/create")
public class VendorPerformanceCreateServlet extends HttpServlet {

    @EJB
    private VendorPerformanceEvaluationServiceLocal evaluationService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to create vendor evaluations.");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");

        String vendorIdStr = req.getParameter("vendorId");
        String scoreStr = req.getParameter("score");
        String evaluationDateStr = req.getParameter("evaluationDate");
        String comments = req.getParameter("comments");

        try {
            Integer vendorId = Integer.parseInt(vendorIdStr);
            BigDecimal score = new BigDecimal(scoreStr);

            LocalDate evaluationDate = null;
            if (evaluationDateStr != null && !evaluationDateStr.trim().isEmpty()) {
                evaluationDate = LocalDate.parse(evaluationDateStr, DATE_FORMATTER);
            }

            evaluationService.createEvaluation(vendorId, userId, score, evaluationDate, comments);

            session.setAttribute("successNotification", "Vendor evaluation created successfully!");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance?vendorId=" + vendorId);

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid number format: {}", e.getMessage());
            session.setAttribute("errorNotification", "Invalid number format");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (DateTimeParseException e) {
            AppLogger.warning("Invalid date format: {}", e.getMessage());
            session.setAttribute("errorNotification", "Invalid date format");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (ValidationException e) {
            AppLogger.warning("Validation error: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Resource not found: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");

        } catch (Exception e) {
            AppLogger.error("Failed to create vendor evaluation", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/vendors/performance");
        }
    }
}