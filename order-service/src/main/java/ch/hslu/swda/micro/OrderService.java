package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public final class OrderService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private final String exchangeName;
    private final BusConnector bus;

    public OrderService() throws IOException, TimeoutException {
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        this.exchangeName = new RabbitMqConfig().getExchange();
        LOG.debug("Exchange name: {}", exchangeName);
        this.bus = new BusConnector();
        LOG.debug("BusConnector created");
        LOG.debug("BusConnector: {}", bus);
        this.bus.connect();
        LOG.debug("Connected to bus");
    }

    public void createOrder() throws IOException, InterruptedException {
        Address address = new Address("Bahnhofstrasse", "1", "Luzern", 6000);
        Customer customer = new Customer(1, "Hans", "Muster", address);
        Employee seller = new Employee(1, "Hans", "Muster", Role.SALES);
        Order order = new OrderBuilder()
                .setId(1)
                .setTimestamp(new Date())
                .setStatus(OrderStatus.NEW)
                .setItems(Collections.emptyList())
                .build();
        final CustomerOrder customerOrder = new CustomerOrder(order, customer, seller);
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(customerOrder);

        LOG.debug("Sending asynchronous message to broker with routing [{}]", OrderRoutes.ORDER_CREATE);
        bus.talkAsync(exchangeName, OrderRoutes.ORDER_CREATE, data);
    }

    public String getOrderStatus(long orderId) throws IOException, InterruptedException {
        String request = String.valueOf(orderId);

        LOG.debug("Sending synchronous message to broker with routing [{}]", OrderRoutes.ORDER_STATUS);
        String response = bus.talkSync(exchangeName, OrderRoutes.ORDER_STATUS, request);

        if (response == null) {
            LOG.debug("Received no response. Timeout occurred. Will retry later");
            return null;
        }
        LOG.debug("Received response for order ID {}: {}", orderId, response);
        return response;
    }

    private void receiveOrderUpdates() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", OrderRoutes.ORDER_UPDATE);
        bus.listenFor(exchangeName, "OrderServiceTemplate <- " + OrderRoutes.ORDER_UPDATE, OrderRoutes.ORDER_UPDATE, new OrderUpdateReceiver(exchangeName, bus));
    }

    @Override
    public void close() {
        bus.close();
    }
}