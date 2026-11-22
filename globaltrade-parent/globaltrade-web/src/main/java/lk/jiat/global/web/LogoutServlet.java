package lk.jiat.global.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.UserContext;  // ← ADD THIS IMPORT

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : "unknown";

        // ===== CLEAR USER CONTEXT =====
        UserContext.clear();

        // ===== SESSION LOGOUT ONLY =====
        if (session != null) {
            session.invalidate();
            AppLogger.info("User logged out: {}", username);
        }

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}