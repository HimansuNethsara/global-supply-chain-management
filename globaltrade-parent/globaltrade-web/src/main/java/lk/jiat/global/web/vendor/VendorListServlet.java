package lk.jiat.global.web.vendor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.service.VendorServiceLocal;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.Country;

import java.io.IOException;
import java.util.List;

@WebServlet("/vendors")
public class VendorListServlet extends HttpServlet {

    @EJB
    private VendorServiceLocal vendorService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<Vendor> vendors = vendorService.findAllVendors();
            req.setAttribute("vendors", vendors);
            req.setAttribute("countryList", Country.getAllDisplayNames());
            AppLogger.info("Loaded {} vendors for vendors list", vendors.size());
        } catch (Exception e) {
            AppLogger.error("Failed to load vendors", e);
            req.setAttribute("error", "Failed to load vendors");
        }

        req.getRequestDispatcher("/vendors/vendors-list.jsp").forward(req, resp);
    }
}