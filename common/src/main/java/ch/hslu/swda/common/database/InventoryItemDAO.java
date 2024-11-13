package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.InventoryItem;
import ch.hslu.swda.common.entities.Order;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class InventoryItemDAO extends GenericDAO<InventoryItem> {
    public InventoryItemDAO(MongoDatabase database) {
        super(database, "inventory", InventoryItem.class);
    }

    public InventoryItem findByUUID(UUID uuid) {
        return collection.find(eq("_id", uuid)).first();
    }

    public List<InventoryItem> getAll() {
        return collection.find().into(new ArrayList<>());
    }
}
