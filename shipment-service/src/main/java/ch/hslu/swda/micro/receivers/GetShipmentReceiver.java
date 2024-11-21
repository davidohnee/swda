package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.ShipmentDAO;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public final class GetShipmentReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(GetShipmentReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final ShipmentDAO shipmentDAO;
    private final ObjectMapper mapper;

    public GetShipmentReceiver(String exchangeName, BusConnector bus, ShipmentDAO shipmentDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.shipmentDAO = shipmentDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.debug("Received message with routing [{}]", route);

        try {
            LOG.debug("Received message: {}", message);

            String data = switch (route) {

                case MessageRoutes.SHIPMENT_GET_ENTITY -> {
                    String cleanedMessage = message.trim().replaceAll("^\"|\"$", "");
                    var shipmentId = UUID.fromString(cleanedMessage);
                    var shipment = shipmentDAO.findByUUID(shipmentId);
                    yield (shipment != null) ? mapper.writeValueAsString(shipment) : "Shipment not found";
                }

                case MessageRoutes.SHIPMENT_GET_ENTITYSET -> {
                    var shipments = shipmentDAO.findAll();
                    yield mapper.writeValueAsString(shipments);
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
