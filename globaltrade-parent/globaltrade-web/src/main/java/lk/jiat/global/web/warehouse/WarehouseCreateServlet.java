package lk.jiat.global.web.warehouse;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.WarehouseServiceLocal;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.Country;

import java.io.IOException;

@WebServlet("/warehouses/create")
public class WarehouseCreateServlet extends HttpServlet {

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

        req.setAttribute("countryList", Country.getAllDisplayNames());
        req.getRequestDispatcher("/warehouses/warehouse-create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String name = req.getParameter("name");
        String location = req.getParameter("location");
        String country = req.getParameter("country");
        String capacityStr = req.getParameter("capacity");

        try {
            Integer capacity = Integer.parseInt(capacityStr);
            warehouseService.createWarehouse(name, location, country, capacity);
            session.setAttribute("successNotification", "Warehouse '" + name + "' created successfully!");
            resp.sendRedirect(req.getContextPath() + "/warehouses");

        } catch (NumberFormatException e) {
            session.setAttribute("errorNotification", "Invalid capacity value");
            resp.sendRedirect(req.getContextPath() + "/warehouses/create");

        } catch (ValidationException | DuplicateResourceException e) {
            session.setAttribute("errorNotification", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/warehouses/create");

        } catch (Exception e) {
            AppLogger.error("Failed to create warehouse", e);
            session.setAttribute("errorNotification", "An unexpected error occurred");
            resp.sendRedirect(req.getContextPath() + "/warehouses/create");
        }
    }
}