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
@PerformanceLogged
public class PerformanceInterceptor {

    @Inject
    private AuditLogDAO auditLogDAO;

    @Inject
    private UserDAO userDAO;

    @AroundInvoke
    public Object measurePerformance(InvocationContext ctx) throws Exception {
        Method method = ctx.getMethod();
        String methodName = method.getName();
        String className = ctx.getTarget().getClass().getSimpleName();

        PerformanceLogged annotation = method.getAnnotation(PerformanceLogged.class);
        long threshold = 500;
        if (annotation != null) {
            threshold = annotation.threshold();
        }

        long startTime = System.nanoTime();

        try {
            Object result = ctx.proceed();
            long durationNanos = System.nanoTime() - startTime;
            long durationMillis = durationNanos / 1_000_000;

            if (durationMillis > threshold) {
                String message = String.format("PERFORMANCE: %s.%s took %d ms (threshold: %d ms)",
                        className, methodName, durationMillis, threshold);

                AppLogger.warning(message);

                try {
                    Integer userId = UserContext.getCurrentUserId();
                    User user = null;
                    if (userId != null) {
                        user = userDAO.findById(userId).orElse(null);
                    }

                    AuditLog auditLog = new AuditLog();
                    auditLog.setUser(user);
                    auditLog.setAction("PERFORMANCE");
                    auditLog.setEntityType("SYSTEM");
                    auditLog.setEntityId(0);
                    auditLog.setDetails(String.format("%s.%s | Duration: %d ms | Threshold: %d ms",
                            className, methodName, durationMillis, threshold));
                    auditLog.setLoggedAt(LocalDateTime.now());
                    auditLogDAO.create(auditLog);
                } catch (Exception e) {
                    AppLogger.error("Failed to log performance: {}", e.getMessage());
                }
            }

            return result;

        } catch (Exception e) {
            long durationNanos = System.nanoTime() - startTime;
            long durationMillis = durationNanos / 1_000_000;

            AppLogger.error("PERFORMANCE: %s.%s failed after %d ms: {}",
                    className, methodName, durationMillis, e.getMessage());
            throw e;
        }
    }
}