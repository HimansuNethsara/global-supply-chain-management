package lk.jiat.global.web.customer;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Customer;
import lk.jiat.global.service.CustomerServiceLocal;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.Country;

import java.io.IOException;
import java.util.List;

@WebServlet("/customers")
public class CustomerListServlet extends HttpServlet {

    @EJB
    private CustomerServiceLocal customerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<Customer> customers = customerService.findAllCustomers();
            req.setAttribute("customers", customers);

            // Add country list for dropdown
            req.setAttribute("countryList", Country.getAllDisplayNames());

            AppLogger.info("Loaded {} customers for customers list", customers.size());
        } catch (Exception e) {
            AppLogger.error("Failed to load customers", e);
            req.setAttribute("error", "Failed to load customers");
        }

        req.getRequestDispatcher("/customers/customers-list.jsp").forward(req, resp);
    }
}