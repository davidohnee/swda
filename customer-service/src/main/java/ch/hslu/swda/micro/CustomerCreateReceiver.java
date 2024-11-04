package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.db.CustomerDBQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerCreateReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerCreateReceiver.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String exchangeName;
    private final BusConnector bus;
    private final CustomerDBQuery dbQuery;

    public CustomerCreateReceiver(final String exchangeName, final BusConnector bus, final CustomerDBQuery dbQuery) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.dbQuery = dbQuery;
    }

    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {

        //log
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Begin message processing", threadName);
        LOG.debug("Received message with routing [{}]", route);

    }
}
