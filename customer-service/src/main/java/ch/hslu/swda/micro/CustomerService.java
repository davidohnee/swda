package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.database.CustomerDAO;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);
    private final String exchangeName;
    private final BusConnector bus;

    private final CustomerDAO customerDAO;

    private boolean running;

    public CustomerService(MongoDatabase database) throws IOException, TimeoutException {
        LOG.debug("Initializing CustomerService...");
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.customerDAO = new CustomerDAO(database);
        this.running = false;
    }

    public void start() throws IOException, TimeoutException {
        try {
            this.bus.connect();
            this.running = true;
            LOG.info("CustomerService connected to RabbitMQ.");
            CustomerMessageListener messageListener = new CustomerMessageListener(this.exchangeName, this.bus, this.customerDAO);
            messageListener.start();
        } catch (IOException e) {
            this.running = false;
            throw new IOException("Failed to start CustomerService.", e);
        } catch (TimeoutException e) {
            this.running = false;
            throw new TimeoutException("Failed to start CustomerService." + e);
        }
    }

    public void stop() {
        LOG.info("Stopping CustomerService and closing connections...");
        this.running = false;
        this.bus.close();
    }

    public boolean isRunning() {
        return this.running;
    }
}
