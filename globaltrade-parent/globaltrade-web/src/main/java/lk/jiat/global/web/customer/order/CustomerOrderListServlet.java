package lk.jiat.global.web.customer.order;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Customer;
import lk.jiat.global.entity.CustomerOrder;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.CustomerOrderServiceLocal;
import lk.jiat.global.service.CustomerServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/customers/orders")
public class CustomerOrderListServlet extends HttpServlet {

    @EJB
    private CustomerOrderServiceLocal customerOrderService;

    @EJB
    private CustomerServiceLocal customerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole().getRoleName();

        try {
            if ("CUSTOMER".equals(role)) {
                Customer customer = customerService.findCustomerByUserId(user.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

                List<CustomerOrder> orders = customerOrderService.getOrdersForCustomer(customer.getCustomerId());

                String customerName = user.getFullName();
                for (CustomerOrder order : orders) {
                    try {
                        int count = customerOrderService.getOrderItemCount(order.getOrderId());
                        order.setItemCount(count);
                        order.setCustomerName(customerName);
                    } catch (Exception e) {
                        AppLogger.warning("Failed to get item count for order {}: {}", order.getOrderId(), e.getMessage());
                        order.setItemCount(0);
                    }
                }

                req.setAttribute("orders", orders);
                req.setAttribute("customerId", customer.getCustomerId());
                AppLogger.info("Loaded {} orders for customer: {}", orders.size(), user.getUsername());

                req.getRequestDispatcher("/customers/customer-orders-list.jsp").forward(req, resp);

            } else {
                resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=CUSTOMER");
            }

        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Customer profile not found for user: {}", user.getUsername());
            req.setAttribute("error", "Customer profile not found. Please contact support.");
            req.getRequestDispatcher("/customers/customer-orders-list.jsp").forward(req, resp);
        } catch (Exception e) {
            AppLogger.error("Failed to load customer orders", e);
            req.setAttribute("error", "Failed to load orders: " + e.getMessage());
            req.getRequestDispatcher("/customers/customer-orders-list.jsp").forward(req, resp);
        }
    }
}