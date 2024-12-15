package ch.hslu.swda.micro.inventory;


import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemCreate;

import java.util.List;
import java.util.UUID;
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

    /**
     * Takes an item from the inventory.
     *
     * @param orderItem The order item.
     * @param receiver  The message receiver.
     */
    void takeItem(OrderItemCreate orderItem, MessageReceiver receiver);

    /**
     * Returns items to the inventory.
     *
     * @param orderItems The order items.
     * @param receiver   The message receiver.
     */
    void returnItems(List<OrderItemCreate> orderItems, MessageReceiver receiver);

    /**
     * Returns an item to the inventory.
     *
     * @param orderItem The order item.
     * @param receiver  The message receiver.
     */
    void returnItem(OrderItemCreate orderItem, MessageReceiver receiver);

    /**
     * Cancels a replenishment.
     *
     * @param trackingId The tracking ID.
     * @param receiver   The message receiver.
     */
    void cancelReplenishment(UUID trackingId, MessageReceiver receiver);
}
