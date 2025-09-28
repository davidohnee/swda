package ch.hslu.swda.micro;

import ch.hslu.swda.bus.MessageReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationReceiver.class);

    public  NotificationReceiver(){}


    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {

        //log
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Begin message processing", threadName);
        LOG.debug("Received message with routing [{}]", route);
        LOG.debug("Received message: {}", message);

        //TODO implement logic
        LOG.info(message);
    }
}
