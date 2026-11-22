package lk.jiat.global.web.customer;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.CustomerServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/customers/delete")
public class CustomerDeleteServlet extends HttpServlet {

    @EJB
    private CustomerServiceLocal customerService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // ===== AUTHORIZATION CHECK: Only LOGISTICS_COORDINATOR can delete =====
        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role)) {
            AppLogger.warning("Unauthorized delete attempt by user with role: {}", role);
            session.setAttribute("errorNotification", "You do not have permission to delete customers.");
            resp.sendRedirect(req.getContextPath() + "/customers");
            return;
        }

        String customerIdStr = req.getParameter("customerId");
        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            session.setAttribute("errorNotification", "Customer ID is required.");
            resp.sendRedirect(req.getContextPath() + "/customers");
            return;
        }

        try {
            Integer customerId = Integer.parseInt(customerIdStr);

            // Check if customer exists first
            if (customerService.findCustomerById(customerId).isEmpty()) {
                throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
            }

            // Delete the customer
            customerService.deleteCustomerWithUser(customerId);
            AppLogger.info("Customer deleted successfully: {}", customerId);
            session.setAttribute("successNotification", "Customer deleted successfully!");

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid customer ID: {}", customerIdStr);
            session.setAttribute("errorNotification", "Invalid customer ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Customer not found: {}", customerIdStr);
            session.setAttribute("errorNotification", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to delete customer", e);
            session.setAttribute("errorNotification", "Failed to delete customer: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/customers");
    }
}