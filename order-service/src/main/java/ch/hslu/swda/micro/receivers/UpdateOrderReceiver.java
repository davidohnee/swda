package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.OrderInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateOrderReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateOrderReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final PersistedOrderDAO persistedOrderDAO;
    private final ObjectMapper mapper;

    public UpdateOrderReceiver(String exchangeName, BusConnector bus, PersistedOrderDAO persistedOrderDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.persistedOrderDAO = persistedOrderDAO;
        this.mapper = new ObjectMapper();
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
        try {
            LOG.debug("Received message: {}", message);
            OrderInfo orderInfo = this.mapper.readValue(message, OrderInfo.class);
            updateOrder(orderInfo);
        } catch (JsonProcessingException e) {
            LOG.error("Error processing message", e);
        }
    }

    private void updateOrder(OrderInfo orderInfo) {
        // TODO: Implement update order -> get order from database based on trackingId
        LOG.debug("Updating order with trackingId: {}", orderInfo.getTrackingId());
    }
}