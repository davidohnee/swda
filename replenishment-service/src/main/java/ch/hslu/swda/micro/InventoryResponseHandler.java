package ch.hslu.swda.micro;

import ch.hslu.swda.entities.InventoryItem;

public interface InventoryResponseHandler {
    void handle(InventoryItem inventoryItem);
}
