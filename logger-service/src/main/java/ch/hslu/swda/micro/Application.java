package ch.hslu.swda.micro;

import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hslu.swda.common.database.MongoDBConnectionManager;
import ch.hslu.swda.common.config.ApplicationConfig;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final int RESTART_DELAY_MS = 5000;

    private static ScheduledExecutorService executorService;
    private static LogService logService;
    private static MongoDatabase database;

    private Application() {}

    public static void main(final String[] args) {
        LOG.info("Application starting...");

        LOG.info("Creating database connection...");
        MongoDBConnectionManager connectionManager = MongoDBConnectionManager.getInstance(
                ApplicationConfig.getConnectionString(),
                ApplicationConfig.getDatabaseName()
        );

        database = connectionManager.getDatabase();

        if (!"OFF".equals(System.getenv("RABBIT"))) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            startAndMonitorService();

            Runtime.getRuntime().addShutdownHook(new Thread(Application::shutdown));
        } else {
            LOG.atWarn().log("RabbitMQ disabled for testing.");
        }
    }

    private static void startAndMonitorService() {
        executorService.schedule(Application::attemptServiceStart, 0, TimeUnit.MILLISECONDS);
    }

    private static void attemptServiceStart() {
        try {
            if (logService == null || !logService.isRunning()) {
                LOG.info("Creating new LoggerService...");
                logService = new LogService(database);
                logService.start();
                LOG.info("LoggerService started successfully.");
            }
        } catch (IOException | TimeoutException e) {
            LOG.error("LoggerService encountered an error and will retry: {}", e.getMessage(), e);
            stopService();
        } catch (Exception ex) {
            LOG.error("Unexpected error in LoggerService, retrying: {}", ex.getMessage(), ex);
            stopService();
        } finally {
            executorService.schedule(Application::attemptServiceStart, RESTART_DELAY_MS, TimeUnit.MILLISECONDS);
        }
    }

    private static void stopService() {
        if (logService!= null) {
            logService.stop();
            logService = null;
        }
    }

    private static void shutdown() {
        LOG.info("Shutting down application...");
        stopService();
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        LOG.info("Application shutdown complete.");
    }
}