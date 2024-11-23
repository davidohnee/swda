package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.PersistedOrder;
import com.mongodb.client.MongoDatabase;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class PersistedOrderDAO extends GenericDAO<PersistedOrder> {
    public PersistedOrderDAO(MongoDatabase database) {
        super(database, "orders", PersistedOrder.class);
    }

    public PersistedOrder findByUUID(UUID orderId) {
        return collection.find(eq("orderId", orderId)).first();
    }
}
