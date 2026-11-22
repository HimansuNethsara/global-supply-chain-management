package lk.jiat.global.web.warehouse;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.WarehouseServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/warehouses/delete")
public class WarehouseDeleteServlet extends HttpServlet {

    @EJB
    private WarehouseServiceLocal warehouseService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to delete warehouses.");
            resp.sendRedirect(req.getContextPath() + "/warehouses");
            return;
        }

        String warehouseIdStr = req.getParameter("warehouseId");
        if (warehouseIdStr == null || warehouseIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/warehouses");
            return;
        }

        try {
            Integer warehouseId = Integer.parseInt(warehouseIdStr);
            warehouseService.deleteWarehouse(warehouseId);
            session.setAttribute("successNotification", "Warehouse deleted successfully!");
        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid warehouse ID");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to delete warehouse", e);
            session.setAttribute("errorNotification", "Failed to delete warehouse");
        }

        resp.sendRedirect(req.getContextPath() + "/warehouses");
    }
}