package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Customer;
import ch.hslu.swda.common.entities.Order;
import com.mongodb.client.MongoDatabase;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class OrderDAO extends GenericDAO<Order> {
    public OrderDAO(MongoDatabase database) {
        super(database, "orders", Order.class);
    }

    public Order findByUUID(UUID uuid) {
        return collection.find(eq("_id", uuid)).first();
    }
}
