package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.database.NotificationDAO;
import ch.hslu.swda.common.routing.MessageRoutes;

import ch.hslu.swda.micro.receivers.CreateNotificationReceiver;
import ch.hslu.swda.micro.receivers.GetNotificationReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NotificationMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationMessageListener.class);

    private final String exchangeName;
    private final BusConnector bus;
    private final NotificationDAO notificationDAO;

    public NotificationMessageListener(String exchangeName, BusConnector bus, NotificationDAO notificationDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.notificationDAO = notificationDAO;
    }

    public void start() throws IOException {
        LOG.info("NotificationService is now listening for messages...");

        this.bus.listenFor(
            this.exchangeName,
            "NotificationService <- notification.get.entityset",
            MessageRoutes.NOTIFICATION_GET_ENTITYSET,
            new GetNotificationReceiver(this.exchangeName, this.bus, this.notificationDAO)
        );

        this.bus.listenFor(
            this.exchangeName,
            "NotificationService <- notification.get.entity",
            MessageRoutes.NOTIFICATION_GET_ENTITY,
            new GetNotificationReceiver(this.exchangeName, this.bus, this.notificationDAO)
        );

        this.bus.listenFor(
            this.exchangeName,
            "NotificationService <- notification.send",
            MessageRoutes.NOTIFICATION_SEND,
            new CreateNotificationReceiver(
                this.exchangeName,
                this.bus,
                this.notificationDAO
            )
        );

    }
}
