package ch.hslu.swda.common.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.*;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoDBConnectionManager {
    private static volatile MongoDBConnectionManager instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoDBConnectionManager(String connectionString, String databaseName) {
        CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new com.mongodb.ConnectionString(connectionString))
            .codecRegistry(pojoCodecRegistry)
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .build();

        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase(databaseName);

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    // double-checked locking singleton implementation
    public static MongoDBConnectionManager getInstance(String connectionString, String databaseName) {
        if (instance == null) {
            synchronized (MongoDBConnectionManager.class) {
                if (instance == null) {
                    instance = new MongoDBConnectionManager(connectionString, databaseName);
                }
            }
        }
        return instance;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
