package ch.hslu.swda.micro;

import ch.hslu.swda.dto.inventory.InventoryItem;

public interface InventoryResponseHandler {
    void handle(InventoryItem inventoryItem);
}
