package lk.jiat.global.web.inventory;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.InventoryServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/inventory/details")
public class InventoryDetailsServlet extends HttpServlet {

    @EJB
    private InventoryServiceLocal inventoryService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String itemIdStr = req.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/inventory");
            return;
        }

        try {
            Integer itemId = Integer.parseInt(itemIdStr);
            InventoryItem item = inventoryService.findItemById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

            req.setAttribute("item", item);
            AppLogger.info("Loaded item details for ID: {}", itemId);

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid item ID: {}", itemIdStr);
            req.setAttribute("error", "Invalid item ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Item not found: {}", itemIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load item details", e);
            req.setAttribute("error", "Failed to load item details");
        }

        req.getRequestDispatcher("/inventory/inventory-item-details.jsp").forward(req, resp);
    }
}