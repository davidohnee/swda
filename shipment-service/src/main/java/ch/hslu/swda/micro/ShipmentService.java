package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.database.ShipmentDAO;
import ch.hslu.swda.common.entities.Order;
import ch.hslu.swda.common.entities.Shipment;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public final class ShipmentService {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentService.class);
    private final String exchangeName;
    private final BusConnector bus;

    private final ShipmentDAO shipmentDAO;
    private final ExecutorService orderProcessingPool;

    private boolean running;

    public ShipmentService(MongoDatabase database) throws IOException, TimeoutException {
        LOG.debug("Initializing ShipmentService...");
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.shipmentDAO = new ShipmentDAO(database);
        this.running = false;
        this.orderProcessingPool = Executors.newCachedThreadPool();
    }

    public void start() throws IOException, TimeoutException {
        try {
            this.bus.connect();
            this.running = true;
            LOG.info("ShipmentService connected to RabbitMQ.");
            ShipmentMessageListener messageListener = new ShipmentMessageListener(this.exchangeName, this.bus, this.shipmentDAO);
            messageListener.start();
        } catch (IOException e) {
            this.running = false;
            throw new IOException("Failed to start ShipmentService.", e);
        } catch (TimeoutException e) {
            this.running = false;
            throw new TimeoutException("Failed to start ShipmentService." + e);
        }
    }

    public void stop() {
        LOG.info("Stopping ShipmentService and closing connections...");
        this.running = false;
        this.bus.close();
    }

    public boolean isRunning() {
        return this.running;
    }
}
