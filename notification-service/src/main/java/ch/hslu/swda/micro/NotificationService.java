package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.database.NotificationDAO;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public final class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);
    private final String exchangeName;
    private final BusConnector bus;

    private final NotificationDAO notificationDAO;
    private final ExecutorService notificationProcessingPool;

    private boolean running;

    public NotificationService(MongoDatabase database) throws IOException, TimeoutException {
        LOG.debug("Initializing NotificationService...");
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.notificationDAO = new NotificationDAO(database);
        this.running = false;
        this.notificationProcessingPool = Executors.newCachedThreadPool();
    }

    public void start() throws IOException, TimeoutException {
        try {
            this.bus.connect();
            this.running = true;
            LOG.info("NotificationService connected to RabbitMQ.");
            NotificationMessageListener messageListener = new NotificationMessageListener(this.exchangeName, this.bus, this.notificationDAO);
            messageListener.start();
        } catch (IOException e) {
            this.running = false;
            throw new IOException("Failed to start NotificationService.", e);
        } catch (TimeoutException e) {
            this.running = false;
            throw new TimeoutException("Failed    to start NotificationService." + e);
        }
    }

    public void stop() {
        LOG.info("Stopping NotificationService and closing connections...");
        this.running = false;
        this.bus.close();
    }

    public boolean isRunning() {
        return this.running;
    }
}
