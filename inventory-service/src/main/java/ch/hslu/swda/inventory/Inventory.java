package ch.hslu.swda.inventory;

import ch.hslu.swda.common.entities.InventoryItem;
import ch.hslu.swda.common.entities.OrderInfo;

import java.util.Collection;

public interface Inventory {
    int REPLENISHMENT_AMOUNT = 100;

    Collection<InventoryItem> getAll();

    OrderInfo take(int productId, int quantity);

    InventoryItem update(int productId, int newQuantity, Integer newReplenishmentThreshold);

    void handleReplenishment(OrderInfo item);

    InventoryItem get(int productId);
}
