package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class OrderValidationReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(OrderValidationReceiver.class);

    private final String exchangeName;
    private final BusConnector bus;
    private final PersistedOrderDAO persistedOrderDAO;
    private final ObjectMapper mapper;

    public OrderValidationReceiver(final String exchangeName, final BusConnector bus, final PersistedOrderDAO persistedOrderDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.persistedOrderDAO = persistedOrderDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {
        LOG.info("Received message with routing [{}]", route);

        try {
            String cleanedMessage = message.trim().replaceAll("^\"|\"$", "");
            LOG.info("Received message: {}", cleanedMessage);
            UUID orderId = UUID.fromString(cleanedMessage);
            var isValid = isOrderValid(orderId);
            String data = isValid ? "true" : "false";

            LOG.info("Sending response: {}", data);
            bus.reply(exchangeName, replyTo, corrId, data);

        } catch (IOException e) {
            LOG.error("Error processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }

    }

    private boolean isOrderValid(final UUID orderId) {
        var status  = persistedOrderDAO.getOrderStatus(orderId);
        return status == Order.StatusEnum.CONFIRMED;
    }

    private void sendErrorResponse(String replyTo, String corrId, String errorMessage) {
        try {
            bus.reply(exchangeName, replyTo, corrId, errorMessage);
        } catch (IOException e) {
            LOG.error("Error sending error response", e);
        }
    }
}
