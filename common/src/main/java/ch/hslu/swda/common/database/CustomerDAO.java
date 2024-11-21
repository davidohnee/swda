package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Customer;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerDAO extends GenericDAO<Customer> {
    public CustomerDAO(MongoDatabase database) {
        super(database, "customers", Customer.class);
    }

    public Customer findByUUID(UUID uuid) {
        return collection.find(eq("_id", uuid)).first();
    }

    public List<Customer> findAll() {
        return collection.find().into(new ArrayList<>());
    }
}
