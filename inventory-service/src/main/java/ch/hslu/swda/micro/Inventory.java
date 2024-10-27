package ch.hslu.swda.micro;

import ch.hslu.swda.entities.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    public static final int REPLENISHMENT_THRESHOLD = 10;
    private final List<InventoryItem> inventory = new ArrayList();

    public Inventory() {
        inventory.add(new InventoryItem(1, 100));
        inventory.add(new InventoryItem(2, 100));
        inventory.add(new InventoryItem(3, 100));
        inventory.add(new InventoryItem(4, 100));
        inventory.add(new InventoryItem(5, 100));
    }

    public List<InventoryItem> getAll() {
        return inventory;
    }

    public InventoryItem getQuantity(long productId) {
        for (InventoryItem i : inventory) {
            if (i.getProductId() == productId) {
                return i;
            }
        }
        return null;
    }

    public TakeFromInventoryResult take(long ProductId, int quantity) {
        for (InventoryItem p : inventory) {
            if (p.getProductId() == ProductId) {
                if (p.getQuantity() < quantity) {
                    return TakeFromInventoryResult.NOT_ENOUGH_QUANTITY;
                }

                p.setQuantity(p.getQuantity() - quantity);

                if (p.getQuantity() < REPLENISHMENT_THRESHOLD) {
                    return TakeFromInventoryResult.REPLENISH_REQUIRED;
                }

                return TakeFromInventoryResult.SUCCESS;
            }
        }

        return TakeFromInventoryResult.NOT_FOUND;
    }
}
