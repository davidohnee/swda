package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OrderUpdateReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(OrderUpdateReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;

    public OrderUpdateReceiver(String exchangeName, BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
    }


    public void receive(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Order order = mapper.readValue(message, Order.class);
            LOG.debug("Received order update: {}", order);
            // Process the order update
        } catch (IOException e) {
            LOG.error("Failed to process order update", e);
        }
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
        LOG.debug("Received message with route [{}], replyTo [{}], corrId [{}]", route, replyTo, corrId);
        this.receive(message);
    }
}