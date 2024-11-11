package ch.hslu.swda.inventory;

import ch.hslu.swda.entities.OrderInfo;
import ch.hslu.swda.dto.replenishment.ReplenishmentOrder;
import ch.hslu.swda.entities.*;
import ch.hslu.swda.micro.senders.ReplenishmentClient;
import ch.hslu.swda.micro.senders.OnItemAvailable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class InMemoryInventory implements Inventory {
    private final Map<Integer, FullInventoryItem> inventory = new HashMap<>();
    private final ReplenishmentClient replenishmentClient;
    private final OnItemAvailable onItemAvailable;
    private final Logger LOG = LoggerFactory.getLogger(InMemoryInventory.class);

    public InMemoryInventory(ReplenishmentClient replenishmentClient, OnItemAvailable onItemAvailable) {
        this.replenishmentClient = replenishmentClient;
        this.onItemAvailable = onItemAvailable;
        this.addSampleData();
    }

    private void addSampleData() {
        for (int i = 0; i < 5; i++) {
            var product = new Product(
                    Product.randomId(),
                    "Product " + i,
                    BigDecimal.valueOf(10 + i));
            inventory.put(product.getId(), new FullInventoryItem(product, 100));
        }
    }

    public Collection<FullInventoryItem> getAll() {
        return inventory.values();
    }

    public OrderInfo take(int productId, int quantity) {
        FullInventoryItem item = this.inventory.get(productId);

        if (item == null) {
            return new OrderInfo(
                    productId,
                    OrderItemStatus.NOT_FOUND,
                    quantity
            );
        }

        if (quantity <= item.getQuantity()) {
            this.update(productId, item.getQuantity() - quantity, item.getReplenishmentThreshold());
            return new OrderInfo(
                    item.getProduct().getId(),
                    OrderItemStatus.DONE,
                    quantity
            );
        }

        this.update(productId, 0, item.getReplenishmentThreshold());

        return new OrderInfo(
                item.getProduct().getId(),
                OrderItemStatus.CONFIRMED,
                quantity - item.getQuantity()
        );
    }

    public FullInventoryItem update(int productId, int newQuantity, Integer newReplenishmentThreshold) {
        FullInventoryItem item = this.inventory.get(productId);
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
                            item.getReplenishmentAmount()
                        ),
                        (orderInfo) -> {
                            LOG.info("Replenishment {} has been processed", orderInfo);
                            synchronized (item) {
                                if (orderInfo.getStatus() == OrderItemStatus.DONE) {
                                    item.handleReplenishment(orderInfo);
                                } else {
                                    item.setReplenishmentTrackingId(orderInfo.getTrackingId());
                                }
                            }
                        }
                );
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return item;
    }

    @Override
    public void handleReplenishment(OrderInfo item) {
        FullInventoryItem inventoryItem = this.inventory.get(item.getProductId());
        if (inventoryItem == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (inventoryItem.isStockReplenishment(item)) {
            inventoryItem.handleReplenishment(item);
            LOG.debug("Product {} replenished", item.getProductId());
            return;
        }

        this.onItemAvailable.onItemAvailable(item);
    }

    @Override
    public FullInventoryItem get(int productId) {
        return this.inventory.get(productId);
    }
}
