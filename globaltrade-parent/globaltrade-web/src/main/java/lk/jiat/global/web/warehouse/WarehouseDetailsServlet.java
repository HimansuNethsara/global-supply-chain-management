package lk.jiat.global.web.warehouse;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.InventoryServiceLocal;
import lk.jiat.global.service.WarehouseServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/warehouses/details")
public class WarehouseDetailsServlet extends HttpServlet {

    @EJB
    private WarehouseServiceLocal warehouseService;

    @EJB
    private InventoryServiceLocal inventoryService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String warehouseIdStr = req.getParameter("warehouseId");
        if (warehouseIdStr == null || warehouseIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/warehouses");
            return;
        }

        try {
            Integer warehouseId = Integer.parseInt(warehouseIdStr);
            Warehouse warehouse = warehouseService.findWarehouseById(warehouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

            // Load inventory items for this warehouse
            List<InventoryItem> inventoryItems = inventoryService.findItemsByWarehouse(warehouseId);

            req.setAttribute("warehouse", warehouse);
            req.setAttribute("inventoryItems", inventoryItems);
            AppLogger.info("Loaded warehouse details for ID: {} with {} inventory items", warehouseId, inventoryItems.size());

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid warehouse ID: {}", warehouseIdStr);
            req.setAttribute("error", "Invalid warehouse ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Warehouse not found: {}", warehouseIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load warehouse details", e);
            req.setAttribute("error", "Failed to load warehouse details");
        }

        req.getRequestDispatcher("/warehouses/warehouse-details.jsp").forward(req, resp);
    }
}