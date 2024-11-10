package ch.hslu.swda.common.config;

import java.util.Optional;

public class ApplicationConfig {
    private static final String DEFAULT_CONNECTION = "mongodb://mongodb:27017";
    private static final String DEFAULT_DATABASE = "data";

    public static String getConnectionString() {
        return Optional.ofNullable(System.getenv("MONGODB_CONNECTION"))
                .filter(s -> !s.isEmpty())
                .orElse(DEFAULT_CONNECTION);
    }

    public static String getDatabaseName() {
        return Optional.ofNullable(System.getenv("MONGODB_DATABASE"))
                .filter(s -> !s.isEmpty())
                .orElse(DEFAULT_DATABASE);
    }
}
