package ch.hslu.swda.inventory;

import ch.hslu.swda.common.database.InventoryItemDAO;
import ch.hslu.swda.common.entities.InventoryItem;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemStatus;
import ch.hslu.swda.common.entities.Product;
import ch.hslu.swda.micro.senders.OnItemAvailable;
import ch.hslu.swda.micro.senders.ReplenishmentClient;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistentInventory implements Inventory {
    private final InventoryItemDAO database;
    private boolean initialised = false;
    private final Map<Integer, InventoryItem> inventory = new HashMap<>();
    private final ReplenishmentClient replenishmentClient;
    private final OnItemAvailable onItemAvailable;
    private final Logger LOG = LoggerFactory.getLogger(PersistentInventory.class);

    public PersistentInventory(MongoDatabase database, ReplenishmentClient replenishmentClient, OnItemAvailable onItemAvailable) {
        LOG.info("Creating persistent inventory");
        this.database = new InventoryItemDAO(database);
        this.replenishmentClient = replenishmentClient;
        this.onItemAvailable = onItemAvailable;
    }

    @Override
    public Collection<InventoryItem> getAll() {
        if (!initialised) {
            LOG.info("Loading inventory from database");
            var inventory = this.database.getAll();
            for (var item : inventory) {
                this.inventory.put(item.getProduct().getId(), item);
            }
            LOG.info("Loaded {} items from database", inventory.size());
            if (this.inventory.isEmpty()) {
                this.addSampleData();
            }
            initialised = true;
        }
        return this.inventory.values();
    }

    private void addSampleData() {
        LOG.info("Adding sample data to inventory");
        for (int i = 0; i < 5; i++) {
            var product = new Product(
                    Product.randomId(),
                    "Product " + i,
                    BigDecimal.valueOf(10 + i));
            var item = new InventoryItem(product, 100);
            inventory.put(product.getId(), item);
            database.create(item);
        }
    }

    @Override
    public OrderInfo take(int productId, int quantity) {
        var item = this.get(productId);
        if (item == null) {
            return new OrderInfo(productId, OrderItemStatus.NOT_FOUND, 0);
        }
        return new OrderInfo(productId, OrderItemStatus.DONE, quantity);
    }

    @Override
    public InventoryItem update(int productId, int newQuantity, Integer newReplenishmentThreshold) {
        return null;
    }

    @Override
    public void handleReplenishment(OrderInfo item) {

    }

    @Override
    public InventoryItem get(int productId) {
        if (!initialised) {
            this.getAll();
        }
        return this.inventory.get(productId);
    }
}
