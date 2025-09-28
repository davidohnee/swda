package ch.hslu.swda.micro.order;

public class OrderValidateException extends RuntimeException {
    public OrderValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderValidateException(String message) {
        super(message);
    }
}
