package ch.hslu.swda.micro;

import ch.hslu.swda.entities.InventoryItem;
import ch.hslu.swda.entities.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inventory {
    public static final int REPLENISHMENT_THRESHOLD = 10;

    private final List<InventoryItem> inventory = new ArrayList<>();
    private final List<Product> productRange = new ArrayList<>();

    public Inventory() {
        for (int i = 0; i < 5; i++) {
            var product = new Product(
                    UUID.randomUUID(),
                    "Product " + i,
                    BigDecimal.valueOf(10 + i));
            productRange.add(product);
            inventory.add(new InventoryItem(product, 100));
        }
    }

    public List<InventoryItem> getAll() {
        return inventory;
    }

    public InventoryItem getQuantity(UUID productId) {
        for (InventoryItem i : inventory) {
            if (i.getProduct().getId().equals(productId)) {
                return i;
            }
        }
        return null;
    }

    public TakeFromInventoryResult take(UUID productId, int quantity) {
        for (InventoryItem p : inventory) {
            if (p.getProduct().getId().equals(productId)) {
                if (p.getCount() < quantity) {
                    return TakeFromInventoryResult.NOT_ENOUGH_QUANTITY;
                }

                p.setCount(p.getCount() - quantity);

                if (p.getCount() < REPLENISHMENT_THRESHOLD) {
                    return TakeFromInventoryResult.REPLENISH_REQUIRED;
                }

                return TakeFromInventoryResult.SUCCESS;
            }
        }

        return TakeFromInventoryResult.NOT_FOUND;
    }
}
