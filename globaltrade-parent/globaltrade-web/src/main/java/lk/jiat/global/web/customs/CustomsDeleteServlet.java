package lk.jiat.global.web.customs;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.CustomsServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/customs/delete")
public class CustomsDeleteServlet extends HttpServlet {

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
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to delete customs documents.");
            resp.sendRedirect(req.getContextPath() + "/customs");
            return;
        }

        String docIdStr = req.getParameter("docId");
        if (docIdStr == null || docIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customs");
            return;
        }

        try {
            Integer docId = Integer.parseInt(docIdStr);
            customsService.deleteDocument(docId);
            session.setAttribute("successNotification", "Customs document deleted successfully!");
        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid document ID");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
        } catch (BusinessException e) {
            // Catch parent BusinessException separately
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to delete customs document", e);
            session.setAttribute("errorNotification", "Failed to delete customs document");
        }

        resp.sendRedirect(req.getContextPath() + "/customs");
    }
}