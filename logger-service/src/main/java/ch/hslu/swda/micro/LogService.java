package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.database.LogDAO;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public final class LogService {

    private static final Logger LOG = LoggerFactory.getLogger(LogService.class);
    private final String exchangeName;
    private final BusConnector bus;

    private final LogDAO logDAO;

    private boolean running;

    public LogService(MongoDatabase database) throws IOException, TimeoutException {
        LOG.debug("Initializing LoggerService...");
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.logDAO = new LogDAO(database);
        this.running = false;
    }

    public void start() throws IOException, TimeoutException {
        try {
            this.bus.connect();
            this.running = true;
            LOG.info("LoggerService connected to RabbitMQ.");
            LogMessageListener messageListener = new LogMessageListener(this.exchangeName, this.bus, this.logDAO);
            messageListener.start();
        } catch (IOException e) {
            this.running = false;
            throw new IOException("Failed to start LoggerService.", e);
        } catch (TimeoutException e) {
            this.running = false;
            throw new TimeoutException("Failed to start LoggerService." + e);
        }
    }

    public void stop() {
        LOG.info("Stopping LoggerService and closing connections...");
        this.running = false;
        this.bus.close();
    }

    public boolean isRunning() {
        return this.running;
    }
}

