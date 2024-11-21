package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.database.ShipmentDAO;
import ch.hslu.swda.common.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class CreateShipmentReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CreateShipmentReceiver.class);

    private final String exchangeName;
    private final BusConnector bus;
    private final ShipmentDAO shipmentDAO;
    private final ObjectMapper mapper;

    public CreateShipmentReceiver(String exchangeName, BusConnector bus, ShipmentDAO shipmentDAO) {
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
            ShipmentCreate shipmentCreate = this.mapper.readValue(message, ShipmentCreate.class);

            //this.shipmentDAO.create(unvalidatedOrder);
            sendResponse(replyTo, corrId, new Shipment());
        } catch (IOException e) {
            LOG.error("Error processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private void sendResponse(String replyTo, String corrId, Shipment shipment) throws IOException {
        String data = this.mapper.writeValueAsString(shipment);
        LOG.debug("Sending response: {}", data);
        bus.reply(exchangeName, replyTo, corrId, data);
    }

    private void sendErrorResponse(String replyTo, String corrId, String errorMessage) {
        try {
            bus.reply(exchangeName, replyTo, corrId, errorMessage);
        } catch (IOException e) {
            LOG.error("Error sending error response", e);
        }
    }
}
