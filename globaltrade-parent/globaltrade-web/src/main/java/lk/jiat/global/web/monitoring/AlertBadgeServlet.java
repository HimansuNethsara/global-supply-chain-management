package lk.jiat.global.web.monitoring;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.global.service.AlertServiceLocal;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/monitoring/alert-count")
public class AlertBadgeServlet extends HttpServlet {

    @EJB
    private AlertServiceLocal alertService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        long activeCount = alertService.getActiveAlertCount();

        try (PrintWriter out = resp.getWriter()) {
            out.write("{\"activeCount\": " + activeCount + "}");
        }
    }
}