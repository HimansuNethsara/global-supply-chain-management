package lk.jiat.global.web.customs;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.CustomsDocument;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.service.CustomsServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/customs")
public class CustomsListServlet extends HttpServlet {

    @EJB
    private CustomsServiceLocal customsService;

    @EJB
    private ShipmentServiceLocal shipmentService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<CustomsDocument> documents = customsService.findAllDocuments();
            List<Shipment> shipments = shipmentService.findAllShipments();

            req.setAttribute("customsDocuments", documents);
            req.setAttribute("shipments", shipments);
            AppLogger.info("Loaded {} customs documents", documents.size());
        } catch (Exception e) {
            AppLogger.error("Failed to load customs documents", e);
            req.setAttribute("error", "Failed to load customs documents");
        }

        req.getRequestDispatcher("/customs/customs-documents-list.jsp").forward(req, resp);
    }
}