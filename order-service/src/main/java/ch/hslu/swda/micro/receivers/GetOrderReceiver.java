package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public final class GetOrderReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(GetOrderReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final OrderDAO orderDAO;
    private final ObjectMapper mapper;

    public GetOrderReceiver(String exchangeName, BusConnector bus, OrderDAO orderDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.orderDAO = orderDAO;
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
                    var order = orderDAO.findByUUID(orderId);
                    yield (order != null) ? mapper.writeValueAsString(order) : "Order not found";
                }
                case MessageRoutes.ORDER_GET_ENTITYSET -> {
                    var orders = orderDAO.findAll();
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
