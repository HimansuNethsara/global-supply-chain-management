package lk.jiat.global.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.UserContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login", "/login.jsp",
            "/register", "/register.jsp",
            "/access-denied.jsp", "/error-404.jsp", "/error-500.jsp",
            "/css/", "/js/", "/images/", "/webfonts/",
            "/favicon.png"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Check if path is public
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);

        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            AppLogger.warning("Unauthenticated access attempt to: {}", path);

            UserContext.clear();

            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Ensure UserContext is set if session has user
        if (session.getAttribute("userId") != null && UserContext.getCurrentUserId() == null) {
            Integer userId = (Integer) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");
            UserContext.setCurrentUser(userId, username);
        }

        chain.doFilter(request, response);
    }
}