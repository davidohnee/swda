package ch.hslu.swda.micro;

import java.util.UUID;

/**
 * Interface for the customer service.
 */
public interface CustomerService {
    /**
     * Checks if a customer exists.
     *
     * @param customerId The customer id.
     * @return True if the customer exists, false otherwise.
     */
    boolean customerExists(UUID customerId);
}
