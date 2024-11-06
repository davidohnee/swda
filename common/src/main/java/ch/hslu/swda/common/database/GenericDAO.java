package ch.hslu.swda.common.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;

public class GenericDAO<T> {
    private final MongoCollection<T> collection;

    public GenericDAO(MongoDatabase database, String collectionName, Class<T> clazz) {
        this.collection = database.getCollection(collectionName, clazz);
    }

    public void create(T entity) {
        collection.insertOne(entity);
    }

    public T read(ObjectId id) {
        return collection.find(eq("_id", id)).first();
    }

    public void update(ObjectId id, T entity) {
        collection.replaceOne(eq("_id", id), entity);
    }

    public void delete(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }
}