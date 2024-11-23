package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.PersistedOrder;
import com.mongodb.client.MongoDatabase;

public class PersistedOrderDAO extends GenericDAO<PersistedOrder> {
    public PersistedOrderDAO(MongoDatabase database) {
        super(database, "orders", PersistedOrder.class);
    }
}
