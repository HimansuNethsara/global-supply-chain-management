package lk.jiat.global.interceptor;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lk.jiat.global.service.ExceptionLoggingServiceLocal;
import lk.jiat.global.util.AppLogger;

@Interceptor
@ExceptionLogged
public class ExceptionLoggingInterceptor {

    @Inject
    private ExceptionLoggingServiceLocal exceptionLoggingService;

    @AroundInvoke
    public Object logExceptions(InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (Exception e) {
            // Log to database
            String entityType = ctx.getTarget().getClass().getSimpleName();
            Object[] params = ctx.getParameters();
            Integer entityId = extractEntityId(params);

            exceptionLoggingService.logException(e, entityType, entityId);
            AppLogger.error("Exception in {}: {}", ctx.getMethod().getName(), e.getMessage());

            throw e;
        }
    }

    private Integer extractEntityId(Object[] params) {
        if (params == null) return null;
        for (Object param : params) {
            if (param instanceof Integer) {
                Integer id = (Integer) param;
                if (id > 0) return id;
            }
            if (param instanceof Long) {
                Long id = (Long) param;
                if (id > 0) return id.intValue();
            }
        }
        return null;
    }
}