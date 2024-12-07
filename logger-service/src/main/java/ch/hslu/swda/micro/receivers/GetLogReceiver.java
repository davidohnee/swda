package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.LogDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetLogReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(GetLogReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final LogDAO logDAO;
    private final ObjectMapper mapper;

    public GetLogReceiver(String exchangeName, BusConnector bus, LogDAO logDAO){
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logDAO = logDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        //ToDo implement logic
        LOG.debug("Received message: [{}]", route);
    }
}
