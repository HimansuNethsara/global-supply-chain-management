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
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.InventoryServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/inventory/update-quantity")
public class InventoryUpdateQuantityServlet extends HttpServlet {

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
            session.setAttribute("errorNotification", "You do not have permission to update inventory.");
            resp.sendRedirect(req.getContextPath() + "/inventory");
            return;
        }

        String itemIdStr = req.getParameter("itemId");
        String quantityStr = req.getParameter("quantity");

        try {
            if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
                throw new ValidationException("Item ID is required");
            }
            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                throw new ValidationException("Quantity is required");
            }

            Integer itemId = Integer.parseInt(itemIdStr);
            Integer newQuantity = Integer.parseInt(quantityStr);

            if (newQuantity < 0) {
                throw new ValidationException("Quantity cannot be negative");
            }

            // Get existing item
            InventoryItem item = inventoryService.findItemById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

            // Update only the quantity
            item.setQuantity(newQuantity);

            // Use the existing update method or create a separate one
            inventoryService.updateItem(
                    itemId,
                    null,
                    null,
                    null,
                    newQuantity,
                    item.getReorderLevel(),
                    item.getUnitPrice()
            );

            AppLogger.info("Quantity updated for item {}: {} -> {} by {}",
                    item.getSku(), item.getQuantity(), newQuantity, session.getAttribute("username"));

            session.setAttribute("successNotification", "Quantity updated successfully!");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid number format");
        } catch (ValidationException | ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to update quantity", e);
            session.setAttribute("errorNotification", "Failed to update quantity");
        }

        resp.sendRedirect(req.getContextPath() + "/inventory");
    }
}