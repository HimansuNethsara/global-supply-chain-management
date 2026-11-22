package lk.jiat.global.exception;

public class InvalidShipmentStateException extends BusinessException {

    public InvalidShipmentStateException(String message) {
        super(message);
    }

    public InvalidShipmentStateException(String message, Throwable cause) {
        super(message, cause);
    }
}