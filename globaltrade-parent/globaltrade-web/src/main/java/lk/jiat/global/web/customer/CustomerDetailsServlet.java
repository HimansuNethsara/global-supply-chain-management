package lk.jiat.global.web.customer;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Customer;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.service.CustomerServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;

@WebServlet("/customers/details")
public class CustomerDetailsServlet extends HttpServlet {

    @EJB
    private CustomerServiceLocal customerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String customerIdStr = req.getParameter("customerId");
        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customers");
            return;
        }

        try {
            Integer customerId = Integer.parseInt(customerIdStr);
            Customer customer = customerService.findCustomerById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

            req.setAttribute("customer", customer);
            AppLogger.info("Loaded customer details for ID: {}", customerId);

        } catch (NumberFormatException e) {
            AppLogger.warning("Invalid customer ID: {}", customerIdStr);
            req.setAttribute("error", "Invalid customer ID");
        } catch (ResourceNotFoundException e) {
            AppLogger.warning("Customer not found: {}", customerIdStr);
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Failed to load customer details", e);
            req.setAttribute("error", "Failed to load customer details");
        }

        req.getRequestDispatcher("/customers/customer-details.jsp").forward(req, resp);
    }
}