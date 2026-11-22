package lk.jiat.global.web.warehouse;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.service.WarehouseServiceLocal;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.Country;

import java.io.IOException;
import java.util.List;

@WebServlet("/warehouses")
public class WarehouseListServlet extends HttpServlet {

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
            List<Warehouse> warehouses = warehouseService.findAllWarehouses();
            req.setAttribute("warehouses", warehouses);
            req.setAttribute("countryList", Country.getAllDisplayNames());
            AppLogger.info("Loaded {} warehouses", warehouses.size());
        } catch (Exception e) {
            AppLogger.error("Failed to load warehouses", e);
            req.setAttribute("error", "Failed to load warehouses");
        }

        req.getRequestDispatcher("/warehouses/warehouses-list.jsp").forward(req, resp);
    }
}