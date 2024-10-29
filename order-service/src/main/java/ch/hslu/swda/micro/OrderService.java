package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.entities.*;
import ch.hslu.swda.model.ContactInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public final class OrderService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private final String exchangeName;
    private final BusConnector bus;
    private long orderId = 1;

    public OrderService() throws IOException, TimeoutException {
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();
    }

    public void createOrder() throws IOException {
        Order order = new OrderBuilder()
                .setId(this.orderId++)
                .setTimestamp(new Date())
                .setStatus(OrderStatus.NEW)
                .setItems(Collections.emptyList())
                .build();
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(order);

        LOG.debug("Sending asynchronous message to broker with routing [{}]", OrderRoutes.ORDER_CREATE);
        bus.talkAsync(this.exchangeName, OrderRoutes.ORDER_CREATE, data);
    }

    public void readOrder() throws IOException {
        LOG.debug("Listening for messages on routing [{}]", MessageRoutes.ORDER_GET_ENTITYSET);
        bus.listenFor(exchangeName, "OrderService <- order.get", MessageRoutes.ORDER_GET_ENTITYSET, new OrderReceiver(this.exchangeName, this.bus));
    }

    @Override
    public void close() {
        bus.close();
    }

    private static class OrderReceiver implements MessageReceiver {

        private final String exchangeName;
        private final BusConnector bus;

        public OrderReceiver(String exchangeName, BusConnector bus) {
            this.exchangeName = exchangeName;
            this.bus = bus;
        }

        /**
         * Listener Methode für Messages.
         *
         * @param route   Route.
         * @param replyTo ReplyTo Route.
         * @param corrId  corrId.
         * @param message Message.
         */
        @Override
        public void onMessageReceived(String route, String replyTo, String corrId, String message) {
            LOG.debug("Received message with routing [{}]", route);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                LOG.debug("Received message: {}", message);
                // Create a list of orders and send it back to the sender
                List<ch.hslu.swda.model.CustomerOrder> orders = createOrderList();
                String data = mapper.writeValueAsString(orders);

                this.bus.reply(this.exchangeName, replyTo, corrId, data);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        private List<ch.hslu.swda.model.CustomerOrder> createOrderList() {
            return List.of(
                new ch.hslu.swda.model.CustomerOrder(
                    new UUID(0, 0),
                    OffsetDateTime.now(),
                    ch.hslu.swda.model.Order.StatusEnum.PENDING,
                    Collections.emptyList(),
                    new BigDecimal("100.0"),
                    ch.hslu.swda.model.Order.OrderTypeEnum.CUSTOMER_ORDER,
                    new ch.hslu.swda.model.Customer(
                        new UUID(0, 0),
                        "John",
                        "Doe",
                        new ch.hslu.swda.model.Address(
                            "Main Street",
                            "1",
                            "1234",
                            "Springfield"
                        ),
                        new ContactInfo()
                    ),

                    new ch.hslu.swda.model.Employee(
                        new UUID(0, 0),
                        "Jane",
                        "Doe",
                        ch.hslu.swda.model.Employee.RoleEnum.SALES
                    )
                )
            );
        }
    }
}