package ch.hslu.swda.inventory;

import ch.hslu.swda.dto.inventory.InventoryItem;
import ch.hslu.swda.entities.OrderInfo;
import ch.hslu.swda.dto.replenishment.ReplenishmentOrder;
import ch.hslu.swda.entities.*;
import ch.hslu.swda.micro.ReplenishmentClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InMemoryInventory implements Inventory {
    private final List<FullInventoryItem> inventory = new ArrayList<>();
    private final List<Product> productRange = new ArrayList<>();
    private final ReplenishmentClient replenishmentClient;

    public InMemoryInventory(ReplenishmentClient replenishmentClient) {
        this.replenishmentClient = replenishmentClient;
        this.addSampleData();
    }

    private void addSampleData() {
        for (int i = 0; i < 5; i++) {
            var product = new Product(
                    Product.randomId(),
                    "Product " + i,
                    BigDecimal.valueOf(10 + i));
            productRange.add(product);
            inventory.add(new FullInventoryItem(product, 100));
        }
    }

    public List<FullInventoryItem> getAll() {
        return inventory;
    }

    public FullInventoryItem get(int productId) {
        for (FullInventoryItem i : inventory) {
            if (i.getProduct().getId() == productId) {
                return i;
            }
        }
        return null;
    }

    public OrderInfo take(int productId, int quantity) {
        FullInventoryItem item = this.get(productId);

        if (item == null) {
            return new OrderInfo(
                    productId,
                    OrderItemStatus.NOT_FOUND
            );
        }

        if (quantity <= item.getQuantity()) {
            this.update(productId, item.getQuantity() - quantity, item.getReplenishmentThreshold());
            return new OrderInfo(
                    item.getProduct().getId(),
                    OrderItemStatus.DONE
            );
        }

        item.preorder(quantity - item.getQuantity());
        this.update(productId, 0, item.getReplenishmentThreshold());

        return new OrderInfo(
                item.getProduct().getId(),
                OrderItemStatus.CONFIRMED
        );
    }

    public FullInventoryItem update(int productId, int newQuantity, Integer newReplenishmentThreshold) {
        FullInventoryItem item = this.get(productId);
        if (item == null) {
            return null;
        }

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (newReplenishmentThreshold != null) {
            item.setReplenishmentThreshold(newReplenishmentThreshold);
        }
        item.setQuantity(newQuantity);

        if (newQuantity < item.getReplenishmentThreshold()) {
            try {
                replenishmentClient.replenish(
                        new ReplenishmentOrder(
                            item.getProduct().getId(),
                            REPLENISHMENT_AMOUNT
                        ),
                        (orderInfo) -> {}
                );
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return item;
    }
}
