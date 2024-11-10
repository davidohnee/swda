package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.model.Order;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public final class OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final OrdersMemory ordersMemory;
    private final ExecutorService orderProcessingPool;
    private boolean running;

    public OrderService(MongoDatabase database) throws IOException, TimeoutException {
        LOG.debug("Initializing OrderService...");
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.ordersMemory = new OrdersMemory();
        this.running = false;
        this.orderProcessingPool = Executors.newCachedThreadPool();
    }

    public void start() throws IOException, TimeoutException {
        try {
            this.bus.connect();
            this.running = true;
            LOG.info("OrderService connected to RabbitMQ.");
            OrderMessageListener messageListener = new OrderMessageListener(
                    this.exchangeName,
                    this.bus,
                    this.ordersMemory
            );
            messageListener.addUnvalidatedOrderListener(this::processOrder);
            messageListener.start();
        } catch (IOException e) {
            this.running = false;
            throw new IOException("Failed to start OrderService.", e);
        } catch (TimeoutException e) {
            this.running = false;
            throw new TimeoutException("Failed to start OrderService." + e);
        }
    }

    private void processOrder(Order order) {
        LOG.info("Processing order: {}", order.getId());
        this.orderProcessingPool.submit(() -> new OrderProcessingWorker(
                this.exchangeName,
                this.bus,
                this.ordersMemory
        ).processOrder(order));
    }

    public void stop() {
        LOG.info("Stopping OrderService and closing connections...");
        this.running = false;
        this.bus.close();
    }

    public boolean isRunning() {
        return this.running;
    }
}
