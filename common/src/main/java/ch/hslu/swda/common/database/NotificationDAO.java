package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Notification;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class NotificationDAO extends GenericDAO<Notification> {

    public NotificationDAO(MongoDatabase database) {
        super(database, "notifications", Notification.class);
    }

    public Notification findByUUID(UUID id) {
        return collection.find(eq("_id", id)).first();
    }

    public List<Notification> findAll() {
        return collection.find().into(new ArrayList<>());
    }

}
