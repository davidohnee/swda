package ch.hslu.swda.micro.customer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for the customer service.
 */
public interface CustomerService {

    /**
     * Validates a customer.
     *
     * @param customerId the customer id
     * @return A completable future with the result of the validation
     */
    CompletableFuture<Boolean> validateCustomer(UUID customerId);
}
