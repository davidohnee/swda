package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.routing.MessageRoutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class NotificationService implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);
    private static final String RECEIVER_START_MSG = "Starting listening for message with routing [{}]";
    private final String exchangeName;
    private final BusConnector bus;

    public NotificationService() throws IOException, TimeoutException {
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);
        // connect to rmq
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();

        //start msg receiver
        this.receiveNotificationSendRequest();
    }

    private void receiveNotificationSendRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.NOTIFICATION_SEND);
        bus.listenFor(exchangeName, "NotificationService <- notification.send", MessageRoutes.NOTIFICATION_SEND, new NotificationReceiver());
    }

    @Override
    public void close() throws Exception {
        bus.close();
    }
}
