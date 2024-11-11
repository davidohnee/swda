package ch.hslu.swda.inventory;

import ch.hslu.swda.dto.inventory.InventoryItem;
import ch.hslu.swda.entities.OrderInfo;
import ch.hslu.swda.entities.Product;

import java.util.UUID;

public class FullInventoryItem extends InventoryItem {
    private UUID replenishmentTrackingId;


    public FullInventoryItem(Product product, int quantity) {
        super(product, quantity);
    }

    public boolean isStockReplenishment(OrderInfo info) {
        return info.getTrackingId() == replenishmentTrackingId;
    }

    public void setReplenishmentTrackingId(UUID trackingId) {
        this.replenishmentTrackingId = trackingId;
    }

    public void handleReplenishment(OrderInfo info) {
        this.quantity += info.getQuantity();
        this.replenishmentTrackingId = null;
    }
}
