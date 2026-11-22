package lk.jiat.global.exception;

public class OrderAlreadyAssignedException extends BusinessException {

    public OrderAlreadyAssignedException(String message) {
        super(message);
    }

    public OrderAlreadyAssignedException(String message, Throwable cause) {
        super(message, cause);
    }
}