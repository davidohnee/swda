package ch.hslu.swda.micro.order;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for the order service.
 */
public interface OrderService {

    /**
     * Validates a order.
     *
     * @param orderId the order id
     * @return A completable future with the result of the validation
     */
    CompletableFuture<Boolean> validateOrder(UUID orderId);
}
