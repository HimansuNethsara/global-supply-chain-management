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
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.InsufficientInventoryException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.service.CustomerOrderServiceLocal;
import lk.jiat.global.service.CustomerServiceLocal;
import lk.jiat.global.service.InventoryServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/customers/orders/create")
public class CustomerOrderCreateServlet extends HttpServlet {

    @EJB
    private CustomerOrderServiceLocal customerOrderService;

    @EJB
    private CustomerServiceLocal customerService;

    @EJB
    private InventoryServiceLocal inventoryService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole().getRoleName();

        if (!"CUSTOMER".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=CUSTOMER");
            return;
        }

        try {
            // Get customer profile
            Customer customer = customerService.findCustomerByUserId(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

            // Get inventory items for selection
            List<InventoryItem> inventoryItems = inventoryService.findAllItems();

            req.setAttribute("customerId", customer.getCustomerId());
            req.setAttribute("inventoryItems", inventoryItems);
            AppLogger.info("Loaded create order page for customer: {}", user.getUsername());

        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Customer profile not found for user: {}", user.getUsername());
            req.setAttribute("error", "Customer profile not found. Please contact support.");
        } catch (Exception e) {
            AppLogger.error("Failed to load create order page", e);
            req.setAttribute("error", "Failed to load page: " + e.getMessage());
        }

        req.getRequestDispatcher("/customers/customer-order-create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole().getRoleName();

        if (!"CUSTOMER".equals(role)) {
            session.setAttribute("errorNotification", "Only customers can create orders.");
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=CUSTOMER");
            return;
        }

        String destination = req.getParameter("destination");
        String notes = req.getParameter("notes");
        String[] itemIds = req.getParameterValues("itemIds");
        String[] quantities = req.getParameterValues("quantities");

        try {
            // Get customer profile
            Customer customer = customerService.findCustomerByUserId(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

            // Validate input
            if (destination == null || destination.trim().isEmpty()) {
                throw new ValidationException("Destination is required");
            }

            if (itemIds == null || itemIds.length == 0 || quantities == null || quantities.length == 0) {
                throw new ValidationException("At least one item is required");
            }

            // Build items map
            Map<Integer, Integer> items = new HashMap<>();
            for (int i = 0; i < itemIds.length; i++) {
                try {
                    Integer itemId = Integer.parseInt(itemIds[i]);
                    Integer quantity = Integer.parseInt(quantities[i]);
                    if (quantity > 0) {
                        items.put(itemId, quantity);
                    }
                } catch (NumberFormatException e) {
                    AppLogger.warning("Invalid item ID or quantity: {}", e.getMessage());
                }
            }

            if (items.isEmpty()) {
                throw new ValidationException("At least one valid item with quantity is required");
            }

            for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                Integer itemId = entry.getKey();
                Integer requestedQuantity = entry.getValue();

                InventoryItem item = inventoryService.findItemById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

                if (item.getQuantity() < requestedQuantity) {
                    throw new ValidationException(
                            "Insufficient inventory for item: " + item.getName() +
                                    ". Available: " + item.getQuantity() + ", Requested: " + requestedQuantity +
                                    ". Please reduce the quantity or choose another item."
                    );
                }
            }

            // Create order
            CustomerOrder order = customerOrderService.createOrder(
                    customer.getCustomerId(),
                    destination.trim(),
                    notes != null ? notes.trim() : null,
                    items
            );

            AppLogger.info("Order created successfully: {} by customer: {}", order.getOrderId(), user.getUsername());
            session.setAttribute("successNotification", "Order #" + order.getOrderId() + " created successfully!");
            resp.sendRedirect(req.getContextPath() + "/customers/orders");

        } catch (ValidationException e) {
            AppLogger.warning("Validation error creating order: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            // Preserve form data
            req.setAttribute("destination", destination);
            req.setAttribute("notes", notes);
            try {
                req.setAttribute("inventoryItems", inventoryService.findAllItems());
                Customer customer = customerService.findCustomerByUserId(user.getUserId()).orElse(null);
                req.setAttribute("customerId", customer != null ? customer.getCustomerId() : null);
            } catch (Exception ex) {
                AppLogger.error("Failed to reload data", ex);
            }
            req.getRequestDispatcher("/customers/customer-order-create.jsp").forward(req, resp);

        } catch (ResourceNotFoundException e) {
            // Handle ResourceNotFoundException separately
            AppLogger.warning("Resource not found: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            try {
                req.setAttribute("inventoryItems", inventoryService.findAllItems());
                Customer customer = customerService.findCustomerByUserId(user.getUserId()).orElse(null);
                req.setAttribute("customerId", customer != null ? customer.getCustomerId() : null);
            } catch (Exception ex) {
                AppLogger.error("Failed to reload data", ex);
            }
            req.getRequestDispatcher("/customers/customer-order-create.jsp").forward(req, resp);

        } catch (InsufficientInventoryException e) {
            AppLogger.warning("Insufficient inventory: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            req.setAttribute("destination", destination);
            req.setAttribute("notes", notes);
            try {
                req.setAttribute("inventoryItems", inventoryService.findAllItems());
                Customer customer = customerService.findCustomerByUserId(user.getUserId()).orElse(null);
                req.setAttribute("customerId", customer != null ? customer.getCustomerId() : null);
            } catch (Exception ex) {
                AppLogger.error("Failed to reload data", ex);
            }
            req.getRequestDispatcher("/customers/customer-order-create.jsp").forward(req, resp);
        } catch (BusinessException e) {
            // Handle other BusinessExceptions
            AppLogger.warning("Business error creating order: {}", e.getMessage());
            session.setAttribute("errorNotification", e.getMessage());
            try {
                req.setAttribute("inventoryItems", inventoryService.findAllItems());
                Customer customer = customerService.findCustomerByUserId(user.getUserId()).orElse(null);
                req.setAttribute("customerId", customer != null ? customer.getCustomerId() : null);
            } catch (Exception ex) {
                AppLogger.error("Failed to reload data", ex);
            }
            req.getRequestDispatcher("/customers/customer-order-create.jsp").forward(req, resp);

        } catch (Exception e) {
            AppLogger.error("Failed to create order", e);
            session.setAttribute("errorNotification", "An unexpected error occurred: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/customers/orders/create");
        }
    }
}