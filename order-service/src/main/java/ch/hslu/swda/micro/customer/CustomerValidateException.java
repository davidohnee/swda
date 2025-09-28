package ch.hslu.swda.micro.customer;

public class CustomerValidateException extends RuntimeException {
    public CustomerValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerValidateException(String message) {
        super(message);
    }
}
