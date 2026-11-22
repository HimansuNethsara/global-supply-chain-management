package lk.jiat.global.web.carrier;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.CarrierServiceLocal;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.ServiceType;

import java.io.IOException;

@WebServlet("/carriers/create")
public class CarrierCreateServlet extends HttpServlet {

    @EJB
    private CarrierServiceLocal carrierService;

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

        req.setAttribute("serviceTypes", ServiceType.getAllDisplayNames());
        req.getRequestDispatcher("/carriers/carrier-create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String carrierName = req.getParameter("carrierName");
        String serviceType = req.getParameter("serviceType");
        String contactInfo = req.getParameter("contactInfo");

        try {
            carrierService.createCarrier(carrierName, serviceType, contactInfo);
            session.setAttribute("successNotification", "Carrier '" + carrierName + "' created successfully!");
            AppLogger.info("Carrier created successfully: {}", carrierName);
            resp.sendRedirect(req.getContextPath() + "/carriers");

        } catch (ValidationException | DuplicateResourceException e) {
            session.setAttribute("errorNotification", e.getMessage());
            req.setAttribute("carrierName", carrierName);
            req.setAttribute("serviceType", serviceType);
            req.setAttribute("contactInfo", contactInfo);
            req.setAttribute("serviceTypes", ServiceType.getAllDisplayNames());
            req.getRequestDispatcher("/carriers/carrier-create.jsp").forward(req, resp);

        } catch (Exception e) {
            AppLogger.error("Failed to create carrier", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/carriers/create");
        }
    }
}