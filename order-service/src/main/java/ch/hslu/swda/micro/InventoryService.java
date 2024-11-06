package ch.hslu.swda.micro;

import java.util.UUID;

/**
 * Interface for the inventory service.
 */
public interface InventoryService {

    /**
     * Checks if an item is available in the inventory.
     *
     * @param itemId   The item id.
     * @param quantity The quantity.
     * @return True if the item is available, false otherwise.
     */
    boolean isItemAvailable(UUID itemId, int quantity);
}
