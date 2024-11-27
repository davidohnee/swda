package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Order;
import ch.hslu.swda.common.entities.PersistedOrder;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Arrays;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class PersistedOrderDAO extends GenericDAO<PersistedOrder> {
    public PersistedOrderDAO(MongoDatabase database) {
        super(database, "orders", PersistedOrder.class);
    }

    public PersistedOrder findByUUID(UUID orderId) {
        return collection.find(eq("orderId", orderId)).first();
    }

    public PersistedOrder findByTrackingId(UUID trackingId) {
        return collection.find(eq("orderItems.trackingId", trackingId)).first();
    }

    public Order.StatusEnum getOrderStatus(UUID orderId) {
        PersistedOrder order = collection.find(eq("orderId", orderId)).first();

        if (order != null) {
            return order.getStatus();
        }

        return null;
    }
}
