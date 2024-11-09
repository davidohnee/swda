package ch.hslu.swda.micro.inventory;

import ch.hslu.swda.model.OrderItemCreate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for the inventory service.
 */
public interface InventoryService {

    /**
     * Takes items from the inventory.
     *
     * @param orderItems The order items.
     * @return The response.
     */
    CompletableFuture<Boolean> takeItems(List<OrderItemCreate> orderItems);
}
