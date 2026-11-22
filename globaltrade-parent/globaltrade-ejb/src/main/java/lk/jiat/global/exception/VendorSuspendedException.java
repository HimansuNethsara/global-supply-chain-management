package lk.jiat.global.exception;

public class VendorSuspendedException extends BusinessException {

    public VendorSuspendedException(String message) {
        super(message);
    }

    public VendorSuspendedException(String message, Throwable cause) {
        super(message, cause);
    }
}