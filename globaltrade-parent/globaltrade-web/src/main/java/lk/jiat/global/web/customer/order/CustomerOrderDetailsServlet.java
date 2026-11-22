package lk.jiat.global.web.customer.order;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.CustomerOrder;
import lk.jiat.global.entity.OrderItem;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.CustomerOrderServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/customers/orders/details")
public class CustomerOrderDetailsServlet extends HttpServlet {

    @EJB
    private CustomerOrderServiceLocal customerOrderService;

    @EJB
    private ShipmentServiceLocal shipmentService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole().getRoleName();

        String orderIdStr = req.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customers/orders");
            return;
        }

        try {
            Integer orderId = Integer.parseInt(orderIdStr);
            CustomerOrder order = customerOrderService.findOrderById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            // Security check: only the order owner or LOGISTICS_COORDINATOR can view
            if ("CUSTOMER".equals(role)) {
                // Check if this order belongs to the logged-in customer
                if (!customerOrderService.isOrderOwnedByCustomer(orderId, order.getCustomer().getCustomerId())) {
                    resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=OWNER");
                    return;
                }
            } else if (!"LOGISTICS_COORDINATOR".equals(role)) {
                resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=LOGISTICS_COORDINATOR");
                return;
            }

            // Get order items
            List<OrderItem> orderItems = customerOrderService.getOrderItems(orderId);

            // Calculate order total
            BigDecimal orderTotal = BigDecimal.ZERO;
            for (OrderItem item : orderItems) {
                BigDecimal itemTotal = item.getItem().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                orderTotal = orderTotal.add(itemTotal);
            }

            // Get shipments linked to this order
            List<Shipment> shipments = new ArrayList<>();
            try {
                // Fetch shipments via shipment_orders junction
                // This requires a method in ShipmentService or custom query
                shipments = shipmentService.findShipmentsByOrderId(orderId);
            } catch (Exception e) {
                AppLogger.warning("Failed to load shipments for order: {}", orderId);
            }

            req.setAttribute("order", order);
            req.setAttribute("orderItems", orderItems);
            req.setAttribute("orderTotal", orderTotal);
            req.setAttribute("shipments", shipments);

            AppLogger.info("Loaded order details for ID: {} by user: {}", orderId, user.getUsername());

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid order ID: {}", orderIdStr);
            req.setAttribute("error", "Invalid order ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Order not found: {}", orderIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load order details", e);
            req.setAttribute("error", "Failed to load order details: " + e.getMessage());
        }

        req.getRequestDispatcher("/customers/customer-order-details.jsp").forward(req, resp);
    }
}