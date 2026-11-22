package lk.jiat.global.web.shipment;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.CustomerOrder;
import lk.jiat.global.entity.User;
import lk.jiat.global.service.CustomerOrderServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/orders/pending")
public class PendingOrdersServlet extends HttpServlet {

    @EJB
    private CustomerOrderServiceLocal customerOrderService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole().getRoleName();

        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=LOGISTICS_COORDINATOR");
            return;
        }

        try {
            List<CustomerOrder> pendingOrders = customerOrderService.getPendingOrders();

            for (CustomerOrder order : pendingOrders) {
                try {
                    int itemCount = customerOrderService.getOrderItemCount(order.getOrderId());
                    order.setItemCount(itemCount);
                } catch (Exception e) {
                    AppLogger.warning("Failed to get item count for order {}: {}", order.getOrderId(), e.getMessage());
                    order.setItemCount(0);
                }
            }

            req.setAttribute("pendingOrders", pendingOrders);
            AppLogger.info("Loaded {} pending orders", pendingOrders.size());
        } catch (Exception e) {
            AppLogger.error("Failed to load pending orders", e);
            req.setAttribute("error", "Failed to load pending orders: " + e.getMessage());
        }

        req.getRequestDispatcher("/orders/pending-orders.jsp").forward(req, resp);
    }
}