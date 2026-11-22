package lk.jiat.global.web.customs;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.CustomsDocumentRejectedException;  // ADD THIS
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.CustomsServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/customs/review")
public class CustomsReviewServlet extends HttpServlet {

    @EJB
    private CustomsServiceLocal customsService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"CUSTOMS_AGENT".equals(role)) {
            session.setAttribute("errorNotification", "Only Customs Agents can review customs documents.");
            resp.sendRedirect(req.getContextPath() + "/customs");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        String docIdStr = req.getParameter("docId");
        String decision = req.getParameter("decision");
        String remarks = req.getParameter("remarks");

        try {
            Integer docId = Integer.parseInt(docIdStr);
            customsService.reviewDocument(docId, decision, remarks, userId);
            session.setAttribute("successNotification", "Customs document reviewed successfully!");
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid document ID");
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (ValidationException | ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (CustomsDocumentRejectedException e) {
            AppLogger.warning("Customs document rejected: {}", e.getMessage());
            session.setAttribute("errorNotification", "Document rejected: " + (e.getRejectionReason() != null ? e.getRejectionReason() : e.getMessage()));
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (BusinessException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (Exception e) {
            AppLogger.error("Failed to review customs document", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/customs");
        }
    }
}