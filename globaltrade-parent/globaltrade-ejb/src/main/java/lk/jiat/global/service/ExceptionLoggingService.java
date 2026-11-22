package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;  // ← ADD THIS
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.ExceptionLogDAO;
import lk.jiat.global.entity.ExceptionLog;
import lk.jiat.global.util.AppLogger;

import java.io.PrintWriter;
import java.io.StringWriter;

@Stateless
public class ExceptionLoggingService implements ExceptionLoggingServiceLocal {

    @Inject
    private ExceptionLogDAO exceptionLogDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @PermitAll
    public void logException(Throwable throwable, String entityType, Integer entityId) {
        try {
            String exceptionType = throwable.getClass().getSimpleName();
            String message = throwable.getMessage() != null ? throwable.getMessage() : "No message";

            // Determine if it's an application or system exception
            String type = throwable instanceof Exception &&
                    !(throwable instanceof RuntimeException) ?
                    "ApplicationException" : "SystemException";

            // Get stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();

            // Truncate stack trace if too long
            if (stackTrace.length() > 4000) {
                stackTrace = stackTrace.substring(0, 4000) + "\n... (truncated)";
            }

            ExceptionLog log = new ExceptionLog();
            log.setExceptionType(exceptionType);
            log.setMessage(type + ": " + message);
            log.setStackTrace(stackTrace);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setOccurredAt(java.time.LocalDateTime.now());
            log.setResolved(false);

            exceptionLogDAO.create(log);
            AppLogger.info("Exception logged to database: {} (Type: {})", exceptionType, type);

        } catch (Exception e) {
            AppLogger.error("Failed to log exception to database", e);
        }
    }

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logException(Throwable throwable) {
        logException(throwable, null, null);
    }
}