package lk.jiat.global.web.carrier;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Carrier;
import lk.jiat.global.service.CarrierServiceLocal;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.List;

@WebServlet("/carriers")
public class CarrierListServlet extends HttpServlet {

    @EJB
    private CarrierServiceLocal carrierService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<Carrier> carriers = carrierService.findAllCarriers();
            req.setAttribute("carriers", carriers);
            AppLogger.info("Loaded {} carriers", carriers.size());
        } catch (Exception e) {
            AppLogger.error("Failed to load carriers", e);
            req.setAttribute("error", "Failed to load carriers");
        }

        req.getRequestDispatcher("/carriers/carriers-list.jsp").forward(req, resp);
    }
}