package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public final class OrderService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private final String exchangeName;
    private final BusConnector bus;
    private long orderId = 1;
    private final OrdersMemory ordersMemory;

    public OrderService() throws IOException, TimeoutException {
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();

        this.ordersMemory = new OrdersMemory();
    }

    // only for testing purposes
    public void createOrder() throws IOException {
        Order order = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now().minusDays(7),
                Order.StatusEnum.PENDING,
                Collections.emptyList(),
                new BigDecimal(1200),
                Order.OrderTypeEnum.CUSTOMER_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "Kelly",
                        "Clark",
                        new Address("Seventh Street", "7D", "8901", "Pinewood"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Leo",
                        "Red",
                        Employee.RoleEnum.MANAGER
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.LOCAL
                )
        );
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        String data = mapper.writeValueAsString(order);

        LOG.debug("Sending asynchronous message to broker with routing [{}]", OrderRoutes.ORDER_CREATE);
        bus.talkAsync(this.exchangeName, OrderRoutes.ORDER_CREATE, data);
    }

    public void readOrder() throws IOException {
        LOG.debug("Listening for messages on routing [{}]", MessageRoutes.ORDER_GET_ENTITYSET);
        bus.listenFor(
                exchangeName,
                "OrderService <- order.get.entityset",
                MessageRoutes.ORDER_GET_ENTITYSET,
                new OrderReceiver(this.exchangeName, this.bus, this.ordersMemory)
        );

        LOG.debug("Listening for messages on routing [{}]", MessageRoutes.ORDER_GET_ENTITY);
        bus.listenFor(
                exchangeName,
                "OrderService <- order.get.entity",
                MessageRoutes.ORDER_GET_ENTITY,
                new OrderReceiver(this.exchangeName, this.bus, this.ordersMemory)
        );
    }

    @Override
    public void close() {
        bus.close();
    }

    private static class OrderReceiver implements MessageReceiver {

        private static final Logger LOG = LoggerFactory.getLogger(OrderReceiver.class);
        private final String exchangeName;
        private final BusConnector bus;
        private final OrdersMemory ordersMemory;
        private final ObjectMapper mapper;

        public OrderReceiver(String exchangeName, BusConnector bus, OrdersMemory ordersMemory) {
            this.exchangeName = exchangeName;
            this.bus = bus;
            this.ordersMemory = ordersMemory;
            this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        }

        @Override
        public void onMessageReceived(String route, String replyTo, String corrId, String message) {
            LOG.debug("Received message with routing [{}]", route);
            try {
                LOG.debug("Received message: {}", message);

                String data = switch (route) {
                    case MessageRoutes.ORDER_GET_ENTITY -> {
                        String cleanedMessage = message.trim().replaceAll("^\"|\"$", "");
                        var orderId = UUID.fromString(cleanedMessage);
                        var order = ordersMemory.getOrderById(orderId);
                        yield (order != null) ? mapper.writeValueAsString(order) : "Order not found";
                    }
                    case MessageRoutes.ORDER_GET_ENTITYSET -> {
                        var orders = ordersMemory.getAllOrders();
                        yield mapper.writeValueAsString(orders);
                    }
                    default -> {
                        LOG.warn("Unknown route: {}", route);
                        yield "Unknown route";
                    }
                };

                LOG.debug("Sending response: {}", data);
                bus.reply(exchangeName, replyTo, corrId, data);

            } catch (IllegalArgumentException e) {
                LOG.error("Invalid UUID format: {}", message, e);
                sendErrorResponse(replyTo, corrId, "Invalid UUID format");
            } catch (IOException e) {
                LOG.error("Error processing message", e);
                sendErrorResponse(replyTo, corrId, "Error processing request");
            }
        }

        private void sendErrorResponse(String replyTo, String corrId, String errorMessage) {
            try {
                bus.reply(exchangeName, replyTo, corrId, errorMessage);
            } catch (IOException e) {
                LOG.error("Error sending error response", e);
            }
        }
    }

}