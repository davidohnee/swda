package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.LogDAO;
import ch.hslu.swda.common.entities.Log;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class GetLogReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(GetLogReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final LogDAO logDAO;
    private final ObjectMapper mapper;

    public GetLogReceiver(String exchangeName, BusConnector bus, LogDAO logDAO) {
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
        LOG.debug("Received message: [{}]", route);

        try {
            String msg = switch (route) {
                case MessageRoutes.LOGGER_GET_ENTITY -> {
                    var logId = deserializeUUID(message);
                    Log log = logDAO.findByUUID(logId);
                    yield (log != null) ? mapper.writeValueAsString(log) : "";
                }
                case MessageRoutes.LOGGER_GET_ENTITYSET -> {
                    if (message.isEmpty()){
                      var logs = logDAO.findAll();
                      yield mapper.writeValueAsString(logs);
                    }
                    var corrUUID = deserializeUUID(message);
                    var logs = logDAO.findByCorrelationId(corrUUID);
                    yield mapper.writeValueAsString(logs);
                }
                default -> {
                    LOG.warn("Unknown route: {}", route);
                    yield "";
                }
            };

            LOG.debug("sending answer with topic [{}] according to replyTo-property", replyTo);
            bus.reply(exchangeName, replyTo, corrId, msg);
        } catch (JsonProcessingException e) {
            LOG.error("Could not process message. Cause {}", e.getMessage(), e);
        } catch (IOException e) {
            LOG.error("Unable to communicate over bus. Cause: {}", e.getMessage(), e);
        }
    }

    private UUID deserializeUUID(final String msg) throws JsonProcessingException {
        return mapper.readValue(msg, UUID.class);
    }
}
