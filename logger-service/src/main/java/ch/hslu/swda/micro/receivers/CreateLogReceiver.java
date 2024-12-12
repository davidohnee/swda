package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.LogDAO;
import ch.hslu.swda.common.entities.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CreateLogReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CreateLogReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final LogDAO logDAO;
    private final ObjectMapper mapper;

    public CreateLogReceiver(String exchangeName, BusConnector bus, LogDAO logDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logDAO = logDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        //log
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Begin message processing", threadName);
        LOG.debug("Received message with routing [{}]", route);

        try {
            var log = mapper.readValue(message, Log.class);
            logDAO.create(log);
            String response = mapper.writeValueAsString(log);
        } catch(JsonProcessingException e){
            LOG.error("Failed to persist log Cause: {}", e.getMessage(), e);
        }
    }
}
