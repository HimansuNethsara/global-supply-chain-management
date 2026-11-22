package lk.jiat.global.web.customer.order;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.CustomerOrder;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.CustomerOrderServiceLocal;
import lk.jiat.global.service.CustomerServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/customers/orders/cancel")
public class CustomerOrderCancelServlet extends HttpServlet {

    @EJB
    private CustomerOrderServiceLocal customerOrderService;

    @EJB
    private CustomerServiceLocal customerService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole().getRoleName();

        // Only CUSTOMER can cancel orders
        if (!"CUSTOMER".equals(role)) {
            session.setAttribute("errorNotification", "Only customers can cancel orders.");
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=CUSTOMER");
            return;
        }

        String orderIdStr = req.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            session.setAttribute("errorNotification", "Order ID is required.");
            resp.sendRedirect(req.getContextPath() + "/customers/orders");
            return;
        }

        try {
            Integer orderId = Integer.parseInt(orderIdStr);

            Integer customerId = (Integer) session.getAttribute("customerId");
            if (customerId == null) {
                // Try to get customer from user
                var customerOpt = customerService.findCustomerByUserId(user.getUserId());
                if (customerOpt.isPresent()) {
                    customerId = customerOpt.get().getCustomerId();
                    session.setAttribute("customerId", customerId);
                } else {
                    session.setAttribute("errorNotification", "Customer profile not found.");
                    resp.sendRedirect(req.getContextPath() + "/customers/orders");
                    return;
                }
            }

            CustomerOrder order = customerOrderService.cancelOrder(orderId, customerId, "Cancelled by customer");

            AppLogger.info("Order {} cancelled by customer: {}", orderId, user.getUsername());
            session.setAttribute("successNotification", "Order #" + orderId + " cancelled successfully!");

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid order ID: {}", orderIdStr);
            session.setAttribute("errorNotification", "Invalid order ID");
        } catch (ValidationException e) {
            AppLogger.warning("Validation error cancelling order: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Order not found: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
        } catch (BusinessException e) {
            AppLogger.warning("Business error cancelling order: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to cancel order", e);
            session.setAttribute("errorNotification", "An unexpected error occurred: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/customers/orders");
    }
}