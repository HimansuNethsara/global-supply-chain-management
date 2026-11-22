package lk.jiat.global.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BusinessException extends Exception {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}