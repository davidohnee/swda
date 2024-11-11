package ch.hslu.swda.micro.senders;

import ch.hslu.swda.dto.inventory.InventoryItem;

public interface InventoryResponseHandler {
    void handle(InventoryItem inventoryItem);
}
