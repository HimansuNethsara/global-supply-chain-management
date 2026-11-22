package lk.jiat.global.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.inject.Inject;
import lk.jiat.global.dao.AuditLogDAO;
import lk.jiat.global.dao.UserDAO;
import lk.jiat.global.entity.AuditLog;
import lk.jiat.global.entity.User;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.UserContext;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Interceptor
@AuditLogged
public class AuditInterceptor {

    @Inject
    private AuditLogDAO auditLogDAO;

    @Inject
    private UserDAO userDAO;

    @AroundInvoke
    public Object audit(InvocationContext ctx) throws Exception {
        Method method = ctx.getMethod();
        String methodName = method.getName();

        // Skip read-only methods
        boolean isReadOnly = methodName.startsWith("get") ||
                methodName.startsWith("find") ||
                methodName.startsWith("is") ||
                methodName.startsWith("has");

        if (isReadOnly) {
            return ctx.proceed();
        }

        // Execute the method first
        Object result = ctx.proceed();

        // Only log successful write operations
        try {
            // ===== GET CURRENT USER FROM THREAD-LOCAL =====
            Integer userId = UserContext.getCurrentUserId();
            String username = UserContext.getCurrentUsername();

            User user = null;
            if (userId != null) {
                user = userDAO.findById(userId).orElse(null);
            }

            // If no user found, try to get system user as fallback
            if (user == null) {
                user = getSystemUser();
            }

            String action = determineAction(methodName);
            String entityType = determineEntityType(ctx.getTarget().getClass());
            Integer entityId = extractEntityId(ctx.getParameters());

            String details = String.format("%s.%s | User: %s | Success",
                    ctx.getTarget().getClass().getSimpleName(),
                    methodName,
                    username != null ? username : "SYSTEM");

            AuditLog auditLog = new AuditLog();
            auditLog.setUser(user);
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId != null ? entityId : 0);
            auditLog.setDetails(details);
            auditLog.setLoggedAt(LocalDateTime.now());

            auditLogDAO.create(auditLog);
            AppLogger.debug("Audit log created: {} - {} (Entity: {}, User: {})",
                    action, entityType, entityId, username != null ? username : "SYSTEM");

        } catch (Exception e) {
            AppLogger.error("Failed to create audit log: {}", e.getMessage());
        }

        return result;
    }

    // ===== SYSTEM USER FALLBACK =====
    private User cachedSystemUser = null;

    private User getSystemUser() {
        if (cachedSystemUser != null) {
            return cachedSystemUser;
        }

        try {
            // Try to find admin user
            var adminUser = userDAO.findByUsername("admin");
            if (adminUser.isPresent()) {
                cachedSystemUser = adminUser.get();
                return cachedSystemUser;
            }

            // Try to find any user
            var allUsers = userDAO.findAll();
            if (!allUsers.isEmpty()) {
                cachedSystemUser = allUsers.get(0);
                return cachedSystemUser;
            }
        } catch (Exception e) {
            AppLogger.debug("Could not find system user: {}", e.getMessage());
        }

        return null;
    }

    private String determineAction(String methodName) {
        if (methodName.startsWith("create") || methodName.startsWith("add") ||
                methodName.startsWith("register") || methodName.startsWith("save") ||
                methodName.startsWith("post")) {
            return "CREATE";
        }
        if (methodName.startsWith("update") || methodName.startsWith("modify") ||
                methodName.startsWith("set") || methodName.startsWith("change") ||
                methodName.startsWith("activate") || methodName.startsWith("suspend") ||
                methodName.startsWith("approve") || methodName.startsWith("reject") ||
                methodName.startsWith("submit") || methodName.startsWith("edit") ||
                methodName.startsWith("fulfill") || methodName.startsWith("assign") ||
                methodName.startsWith("revoke")) {
            return "UPDATE";
        }
        if (methodName.startsWith("delete") || methodName.startsWith("remove") ||
                methodName.startsWith("cancel")) {
            return "DELETE";
        }
        if (methodName.startsWith("login") || methodName.startsWith("authenticate")) {
            return "LOGIN";
        }
        if (methodName.startsWith("logout")) {
            return "LOGOUT";
        }
        if (methodName.contains("Timer") || methodName.contains("timer") ||
                methodName.startsWith("check") || methodName.startsWith("run") ||
                methodName.startsWith("evaluate") || methodName.startsWith("process") ||
                methodName.startsWith("recalculate") || methodName.startsWith("resolve")) {
            return "TIMER";
        }
        return "VIEW";
    }

    private String determineEntityType(Class<?> targetClass) {
        String className = targetClass.getSimpleName();
        if (className.contains("Shipment")) return "SHIPMENT";
        if (className.contains("Vendor")) return "VENDOR";
        if (className.contains("Inventory") || className.contains("Item")) return "INVENTORY";
        if (className.contains("Customer")) return "CUSTOMER";
        if (className.contains("Order")) return "ORDER";
        if (className.contains("Customs") || className.contains("Document")) return "CUSTOMS";
        if (className.contains("User")) return "USER";
        if (className.contains("Warehouse")) return "WAREHOUSE";
        if (className.contains("Carrier")) return "CARRIER";
        if (className.contains("Route") || className.contains("RoutePlan")) return "ROUTE";
        if (className.contains("Alert")) return "ALERT";
        if (className.contains("Timer")) return "TIMER";
        if (className.contains("Performance")) return "SYSTEM";
        return "SYSTEM";
    }

    private Integer extractEntityId(Object[] params) {
        if (params == null || params.length == 0) {
            return null;
        }

        for (Object param : params) {
            if (param == null) continue;

            if (param instanceof Integer) {
                Integer id = (Integer) param;
                if (id > 0) {
                    return id;
                }
            }
            if (param instanceof Long) {
                Long id = (Long) param;
                if (id > 0) {
                    return id.intValue();
                }
            }
        }
        return null;
    }
}