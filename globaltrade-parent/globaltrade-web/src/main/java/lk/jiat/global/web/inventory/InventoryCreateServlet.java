package lk.jiat.global.web.inventory;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.InventoryServiceLocal;
import lk.jiat.global.service.WarehouseServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/inventory/create")
public class InventoryCreateServlet extends HttpServlet {

    @EJB
    private InventoryServiceLocal inventoryService;

    @EJB
    private WarehouseServiceLocal warehouseService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role) && !"WAREHOUSE_MANAGER".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=LOGISTICS_COORDINATOR");
            return;
        }

        try {
            List<Warehouse> warehouses = warehouseService.findAllWarehouses();
            req.setAttribute("warehouses", warehouses);
        } catch (Exception e) {
            AppLogger.error("Failed to load warehouses", e);
        }

        req.getRequestDispatcher("/inventory/inventory-item-create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String name = req.getParameter("name");
        String sku = req.getParameter("sku");
        String warehouseIdStr = req.getParameter("warehouseId");
        String quantityStr = req.getParameter("quantity");
        String reorderLevelStr = req.getParameter("reorderLevel");
        String unitPriceStr = req.getParameter("unitPrice");

        try {
            Integer warehouseId = Integer.parseInt(warehouseIdStr);
            Integer quantity = Integer.parseInt(quantityStr);
            Integer reorderLevel = reorderLevelStr != null && !reorderLevelStr.isEmpty()
                    ? Integer.parseInt(reorderLevelStr) : 10;
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);

            inventoryService.createItem(sku, name, warehouseId, quantity, reorderLevel, unitPrice);
            session.setAttribute("successNotification", "Item '" + name + "' created successfully!");
            resp.sendRedirect(req.getContextPath() + "/inventory");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid number format");
            // Preserve form data
            req.setAttribute("name", name);
            req.setAttribute("sku", sku);
            req.setAttribute("warehouseId", warehouseIdStr);
            req.setAttribute("quantity", quantityStr);
            req.setAttribute("reorderLevel", reorderLevelStr);
            req.setAttribute("unitPrice", unitPriceStr);
            try {
                req.setAttribute("warehouses", warehouseService.findAllWarehouses());
            } catch (Exception ex) {
                AppLogger.error("Failed to load warehouses", ex);
            }
            req.getRequestDispatcher("/inventory/inventory-item-create.jsp").forward(req, resp);

        } catch (ValidationException | DuplicateResourceException | ResourceNotFoundException e) {
            session.setAttribute("errorNotification", e.getMessage());
            // Preserve form data for validation errors
            req.setAttribute("name", name);
            req.setAttribute("sku", sku);
            req.setAttribute("warehouseId", warehouseIdStr);
            req.setAttribute("quantity", quantityStr);
            req.setAttribute("reorderLevel", reorderLevelStr);
            req.setAttribute("unitPrice", unitPriceStr);
            try {
                req.setAttribute("warehouses", warehouseService.findAllWarehouses());
            } catch (Exception ex) {
                AppLogger.error("Failed to load warehouses", ex);
            }
            req.getRequestDispatcher("/inventory/inventory-item-create.jsp").forward(req, resp);

        } catch (Exception e) {
            AppLogger.error("Failed to create inventory item", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/inventory/create");
        }
    }
}