package ch.hslu.swda.inventory;

import ch.hslu.swda.entities.OrderInfo;

import java.util.List;

public interface Inventory {
    int REPLENISHMENT_AMOUNT = 100;

    List<FullInventoryItem> getAll();

    FullInventoryItem get(int productId);

    OrderInfo take(int productId, int quantity);

    FullInventoryItem update(int productId, int newQuantity, Integer newReplenishmentThreshold);
}
