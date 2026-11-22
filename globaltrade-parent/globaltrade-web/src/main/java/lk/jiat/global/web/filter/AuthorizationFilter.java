package lk.jiat.global.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.User;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    private static final Map<String, List<String>> ROLE_MAP = new HashMap<>();

    static {
        // Dashboard - all authenticated users (including root)
        ROLE_MAP.put("/", Arrays.asList("LOGISTICS_COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REP", "CUSTOMER"));
        ROLE_MAP.put("/dashboard", Arrays.asList("LOGISTICS_COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REP", "CUSTOMER"));

        // Admin section - only Logistics Coordinators
        ROLE_MAP.put("/admin/", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/admin/users", List.of("LOGISTICS_COORDINATOR"));

        // Shipment management
        ROLE_MAP.put("/shipments", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER", "CUSTOMS_AGENT", "VENDOR_REP", "CUSTOMER"));
        ROLE_MAP.put("/shipments/details", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER", "CUSTOMS_AGENT", "VENDOR_REP", "CUSTOMER"));
        ROLE_MAP.put("/shipments/create", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/shipments/update", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/shipments/status-update", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/shipments/status-history", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER", "CUSTOMS_AGENT", "VENDOR_REP", "CUSTOMER"));
        ROLE_MAP.put("/shipments/delete", List.of("LOGISTICS_COORDINATOR"));

        // Inventory management
        ROLE_MAP.put("/inventory", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/inventory/details", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/inventory/create", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/inventory/delete", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/inventory/update-quantity", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));

        // Vendor management
        ROLE_MAP.put("/vendors", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER", "CUSTOMS_AGENT"));
        ROLE_MAP.put("/vendors/details", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER", "CUSTOMS_AGENT", "VENDOR_REP"));
        ROLE_MAP.put("/vendors/create", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/vendors/delete", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/vendors/update", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/vendors/performance", List.of("LOGISTICS_COORDINATOR"));

        // Customer management
        ROLE_MAP.put("/customers", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER", "CUSTOMS_AGENT"));
        ROLE_MAP.put("/customers/details", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER", "CUSTOMS_AGENT", "VENDOR_REP"));
        ROLE_MAP.put("/customers/create", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/customers/delete", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/customers/update", List.of("LOGISTICS_COORDINATOR"));

        // Customer Orders
        ROLE_MAP.put("/customers/orders", Arrays.asList("CUSTOMER", "LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/customers/orders/create", List.of("CUSTOMER"));
        ROLE_MAP.put("/customers/orders/cancel", List.of("CUSTOMER"));
        ROLE_MAP.put("/customers/orders/details", Arrays.asList("CUSTOMER", "LOGISTICS_COORDINATOR"));

        // Orders
        ROLE_MAP.put("/orders/", Arrays.asList("LOGISTICS_COORDINATOR", "CUSTOMER"));
        ROLE_MAP.put("/orders/pending", List.of("LOGISTICS_COORDINATOR"));

        // Customs management
        ROLE_MAP.put("/customs", Arrays.asList("LOGISTICS_COORDINATOR", "CUSTOMS_AGENT"));
        ROLE_MAP.put("/customs/details", Arrays.asList("LOGISTICS_COORDINATOR", "CUSTOMS_AGENT"));
        ROLE_MAP.put("/customs/create", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/customs/submit", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/customs/review", List.of("CUSTOMS_AGENT"));
        ROLE_MAP.put("/customs/delete", List.of("LOGISTICS_COORDINATOR"));

        // Warehouses
        ROLE_MAP.put("/warehouses", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/warehouses/details", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/warehouses/create", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));
        ROLE_MAP.put("/warehouses/delete", List.of("LOGISTICS_COORDINATOR"));

        // Carriers
        ROLE_MAP.put("/carriers", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/carriers/create", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/carriers/delete", List.of("LOGISTICS_COORDINATOR"));

        // Monitoring
        ROLE_MAP.put("/monitoring/", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/monitoring/alerts", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/monitoring/timers", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/monitoring/alert/action", List.of("LOGISTICS_COORDINATOR"));
        ROLE_MAP.put("/monitoring/alert-count", Arrays.asList("LOGISTICS_COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REP", "CUSTOMER"));

        // Route planning
        ROLE_MAP.put("/route-plan", Arrays.asList("LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"));

        // User profile - all authenticated users
        ROLE_MAP.put("/user/", Arrays.asList("LOGISTICS_COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REP", "CUSTOMER"));

        // Add more specific mappings as needed
        ROLE_MAP.put("/user/user-profile", Arrays.asList("LOGISTICS_COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REP", "CUSTOMER"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // If path is empty or just "/", treat as dashboard
        if (path == null || path.isEmpty() || "/".equals(path)) {
            path = "/dashboard";
            AppLogger.info("AuthorizationFilter - Redirecting root path to dashboard");
        }

        // If no session or no user, let AuthenticationFilter handle it
        if (session == null || session.getAttribute("user") == null) {
            chain.doFilter(request, response);
            return;
        }

        // Get user role - try multiple sources
        String userRole = (String) session.getAttribute("role");

        // If role not in session, get it from User object
        if (userRole == null) {
            User user = (User) session.getAttribute("user");
            if (user != null && user.getRole() != null) {
                userRole = user.getRole().getRoleName();
                session.setAttribute("role", userRole);
                AppLogger.info("AuthorizationFilter - Retrieved role from User object: {}", userRole);
            }
        }

        // If still null, log and redirect
        if (userRole == null) {
            AppLogger.warning("AuthorizationFilter - No role found for user, path: {}", path);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Find matching role requirement
        List<String> allowedRoles = null;
        for (Map.Entry<String, List<String>> entry : ROLE_MAP.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                allowedRoles = entry.getValue();
                break;
            }
        }

        // If no specific role required, allow access
        if (allowedRoles == null) {
            AppLogger.info("AuthorizationFilter - No role restriction for path: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Check if user has required role
        if (!allowedRoles.contains(userRole)) {
            String requiredRole = allowedRoles.get(0);
            AppLogger.warning("AuthorizationFilter - Access Denied: Path={}, UserRole={}, Required={}",
                    path, userRole, requiredRole);
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=" + requiredRole);
            return;
        }

        AppLogger.info("AuthorizationFilter - Access Granted: Path={}, UserRole={}", path, userRole);
        chain.doFilter(request, response);
    }
}