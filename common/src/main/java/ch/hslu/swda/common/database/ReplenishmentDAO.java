package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.InventoryItem;
import ch.hslu.swda.common.entities.SimpleReplenishmentOrder;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReplenishmentDAO extends GenericDAO<SimpleReplenishmentOrder> {
    public ReplenishmentDAO(MongoDatabase database) {
        super(database, "replenishments", SimpleReplenishmentOrder.class);
    }

    public void update(SimpleReplenishmentOrder entity) {
        super.update(entity.getId(), entity);
    }

    public List<SimpleReplenishmentOrder> getAll() {
        return collection.find().into(new ArrayList<>());
    }
}
