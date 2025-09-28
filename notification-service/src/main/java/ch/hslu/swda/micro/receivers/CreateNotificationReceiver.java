package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.NotificationDAO;
import ch.hslu.swda.common.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class CreateNotificationReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CreateNotificationReceiver.class);

    private final String exchangeName;
    private final BusConnector bus;
    private final NotificationDAO notificationDAO;
    private final ObjectMapper mapper;

    public CreateNotificationReceiver(String exchangeName, BusConnector bus, NotificationDAO notificationDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.notificationDAO = notificationDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.debug("Received message with routing [{}]", route);

        try {
            LOG.debug("Received message: {}", message);
            Notification notification = this.mapper.readValue(message, Notification.class);
            LOG.debug("Received notification: {}", notification);
            this.notificationDAO.create(notification);
            //sendResponse(replyTo, corrId, notification);
        } catch (IOException e) {
            LOG.error("Error processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private void sendResponse(String replyTo, String corrId, Notification notification) throws IOException {
        String data = this.mapper.writeValueAsString(notification);
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
