package ch.hslu.swda.micro.inventory;


import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemCreate;

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
     * @return An array of {@link OrderInfo} objects.
     */
    CompletableFuture<OrderInfo[]> takeItems(List<OrderItemCreate> orderItems);
}
