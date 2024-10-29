package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
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
        LOG.debug("Reading order from broker with routing [{}]", OrderRoutes.ORDER_CREATE);
        bus.listenFor(exchangeName, "OrderService <- order.create", OrderRoutes.ORDER_CREATE, new OrderReceiver());
    }

    @Override
    public void close() {
        bus.close();
    }

    private static class OrderReceiver implements MessageReceiver {
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
            try {
                Order order = mapper.readValue(message, Order.class);
                LOG.info("Received order: {}", order);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}