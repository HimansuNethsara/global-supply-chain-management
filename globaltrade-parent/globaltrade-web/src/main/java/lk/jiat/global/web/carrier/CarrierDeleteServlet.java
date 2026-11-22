package lk.jiat.global.web.carrier;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.CarrierServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/carriers/delete")
public class CarrierDeleteServlet extends HttpServlet {

    @EJB
    private CarrierServiceLocal carrierService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to delete carriers.");
            resp.sendRedirect(req.getContextPath() + "/carriers");
            return;
        }

        String carrierIdStr = req.getParameter("carrierId");
        if (carrierIdStr == null || carrierIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/carriers");
            return;
        }

        try {
            Integer carrierId = Integer.parseInt(carrierIdStr);
            carrierService.deleteCarrier(carrierId);
            session.setAttribute("successNotification", "Carrier deleted successfully!");
        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid carrier ID");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to delete carrier", e);
            session.setAttribute("errorNotification", "Failed to delete carrier");
        }

        resp.sendRedirect(req.getContextPath() + "/carriers");
    }
}