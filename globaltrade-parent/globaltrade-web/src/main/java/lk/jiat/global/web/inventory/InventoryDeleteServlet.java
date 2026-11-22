package lk.jiat.global.web.inventory;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.InventoryServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/inventory/delete")
public class InventoryDeleteServlet extends HttpServlet {

    @EJB
    private InventoryServiceLocal inventoryService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role) && !"WAREHOUSE_MANAGER".equals(role)) {
            session.setAttribute("errorNotification", "You do not have permission to delete inventory items.");
            resp.sendRedirect(req.getContextPath() + "/inventory");
            return;
        }

        String itemIdStr = req.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/inventory");
            return;
        }

        try {
            Integer itemId = Integer.parseInt(itemIdStr);
            inventoryService.deleteItem(itemId);
            session.setAttribute("successNotification", "Item deleted successfully!");
        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid item ID");
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to delete item", e);
            session.setAttribute("errorNotification", "Failed to delete item");
        }

        resp.sendRedirect(req.getContextPath() + "/inventory");
    }
}