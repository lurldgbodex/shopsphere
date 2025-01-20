package shopsphere_logging.exception;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super((message));
    }
}
