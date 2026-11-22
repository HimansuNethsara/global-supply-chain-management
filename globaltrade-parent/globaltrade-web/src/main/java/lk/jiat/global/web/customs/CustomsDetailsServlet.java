package lk.jiat.global.web.customs;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.CustomsDocument;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.CustomsServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/customs/details")
public class CustomsDetailsServlet extends HttpServlet {

    @EJB
    private CustomsServiceLocal customsService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String docIdStr = req.getParameter("docId");
        if (docIdStr == null || docIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customs");
            return;
        }

        try {
            Integer docId = Integer.parseInt(docIdStr);
            CustomsDocument document = customsService.findDocumentById(docId)
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

            req.setAttribute("document", document);
            AppLogger.info("Loaded customs document details for ID: {}", docId);

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid document ID: {}", docIdStr);
            req.setAttribute("error", "Invalid document ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Document not found: {}", docIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load document details", e);
            req.setAttribute("error", "Failed to load document details");
        }

        req.getRequestDispatcher("/customs/customs-document-details.jsp").forward(req, resp);
    }
}