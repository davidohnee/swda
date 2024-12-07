package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Log;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class LogDAO extends GenericDAO<Log> {
    public LogDAO(MongoDatabase database) {
        super(database, "logs", Log.class);
    }

    public Log findByUUID(UUID uuid) {
        return collection.find(eq("_id", uuid)).first();
    }

    public List<Log> findAll() {
        return collection.find().into(new ArrayList<>());
    }
}

