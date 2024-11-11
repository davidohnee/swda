package ch.hslu.swda.inventory;

import ch.hslu.swda.dto.inventory.InventoryItem;
import ch.hslu.swda.entities.OrderInfo;

import java.util.Collection;
import java.util.List;

public interface Inventory {
    int REPLENISHMENT_AMOUNT = 100;

    Collection<FullInventoryItem> getAll();

    OrderInfo take(int productId, int quantity);

    FullInventoryItem update(int productId, int newQuantity, Integer newReplenishmentThreshold);

    void handleReplenishment(OrderInfo item);

    FullInventoryItem get(int productId);
}
