package lk.jiat.global.web.inventory;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.service.InventoryServiceLocal;
import lk.jiat.global.service.WarehouseServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/inventory")
public class InventoryListServlet extends HttpServlet {

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

        try {
            List<InventoryItem> items = inventoryService.findAllItems();
            List<Warehouse> warehouses = warehouseService.findAllWarehouses();

            req.setAttribute("inventoryItems", items);
            req.setAttribute("warehouses", warehouses);
            AppLogger.info("Loaded {} inventory items and {} warehouses", items.size(), warehouses.size());
        } catch (Exception e) {
            AppLogger.error("Failed to load inventory", e);
            req.setAttribute("error", "Failed to load inventory");
        }

        req.getRequestDispatcher("/inventory/inventory-list.jsp").forward(req, resp);
    }
}