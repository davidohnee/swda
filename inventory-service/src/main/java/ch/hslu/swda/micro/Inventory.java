package ch.hslu.swda.micro;

import ch.hslu.swda.entities.InventoryItem;
import ch.hslu.swda.entities.Product;
import ch.hslu.swda.entities.ReplenishmentOrder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inventory {
    public static final int REPLENISHMENT_THRESHOLD = 10;
    public static final int REPLENISHMENT_AMOUNT = 100;

    private final List<InventoryItem> inventory = new ArrayList<>();
    private final List<Product> productRange = new ArrayList<>();
    private final ReplenishmentClientService replenishmentClientService;

    public Inventory(ReplenishmentClientService replenishmentClientService) {
        this.replenishmentClientService = replenishmentClientService;
        this.addSampleData();
    }

    private void addSampleData() {
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

    public InventoryItem update(UUID productId, int newCount) {
        for (InventoryItem item : inventory) {
            if (item.getProduct().getId().equals(productId)) {
                item.setCount(newCount);

                if (item.getCount() < REPLENISHMENT_THRESHOLD) {
                    try {
                        replenishmentClientService.replenish(
                                new ReplenishmentOrder(
                                    item.getProduct().getId().hashCode(), // FIXME: This is terrible design
                                    REPLENISHMENT_AMOUNT
                                )
                        );
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                return item;
            }
        }

        return null;
    }
}
