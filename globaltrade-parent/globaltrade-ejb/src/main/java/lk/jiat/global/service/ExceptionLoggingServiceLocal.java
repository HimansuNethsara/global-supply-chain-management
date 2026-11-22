package lk.jiat.global.service;

import jakarta.ejb.Local;

@Local
public interface ExceptionLoggingServiceLocal {

    void logException(Throwable throwable, String entityType, Integer entityId);

    void logException(Throwable throwable);
}