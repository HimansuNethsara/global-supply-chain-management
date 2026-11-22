package lk.jiat.global.web.customs;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.CustomsDocumentRejectedException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.CustomsServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/customs/submit")
public class CustomsSubmitServlet extends HttpServlet {

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
        String docIdStr = req.getParameter("docId");
        String status = req.getParameter("status");

        // Validate input
        if (docIdStr == null || docIdStr.trim().isEmpty()) {
            session.setAttribute("errorNotification", "Document ID is required");
            resp.sendRedirect(req.getContextPath() + "/customs");
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            session.setAttribute("errorNotification", "New status is required");
            resp.sendRedirect(req.getContextPath() + "/customs");
            return;
        }

        try {
            Integer docId = Integer.parseInt(docIdStr);
            Integer userId = (Integer) session.getAttribute("userId");

            // Determine which action to take based on role
            if ("LOGISTICS_COORDINATOR".equals(role)) {
                // Logistics Coordinator: Can only submit (PENDING -> SUBMITTED)
                if (!"SUBMITTED".equals(status)) {
                    session.setAttribute("errorNotification", "Logistics Coordinators can only submit documents (PENDING → SUBMITTED)");
                    resp.sendRedirect(req.getContextPath() + "/customs");
                    return;
                }
                customsService.submitDocument(docId, userId);
                session.setAttribute("successNotification", "Customs document submitted successfully!");

            } else if ("CUSTOMS_AGENT".equals(role)) {
                // Customs Agent: Can only approve or reject (SUBMITTED -> APPROVED/REJECTED)
                if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
                    session.setAttribute("errorNotification", "Customs Agents can only APPROVE or REJECT submitted documents");
                    resp.sendRedirect(req.getContextPath() + "/customs");
                    return;
                }
                customsService.reviewDocument(docId, status, null, userId);
                session.setAttribute("successNotification", "Customs document " + status.toLowerCase() + " successfully!");

            } else {
                session.setAttribute("errorNotification", "You do not have permission to update customs documents.");
                resp.sendRedirect(req.getContextPath() + "/customs");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid document ID");
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (CustomsDocumentRejectedException e) {
            AppLogger.warning("Document was previously rejected: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (ValidationException | ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (BusinessException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customs");

        } catch (Exception e) {
            AppLogger.error("Failed to update customs document", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/customs");
        }
    }
}