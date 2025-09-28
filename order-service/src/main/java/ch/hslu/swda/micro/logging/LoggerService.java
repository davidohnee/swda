package ch.hslu.swda.micro.logging;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.entities.Log;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class LoggerService {

    private static final Logger LOG = LoggerFactory.getLogger(LoggerService.class);
    private final String source;
    private final BusConnector bus;
    private final ObjectMapper mapper;
    private final String exchangeName;

    public LoggerService(String source, String exchangeName, BusConnector bus){
        this.source = source;
        this.bus = bus;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.exchangeName = exchangeName;
    }

    public void debug(String message, UUID... correlationIDs){
        log(message, Log.LevelEnum.DEBUG, correlationIDs);
    }

    public void info(String message, UUID... correlationIDs){
        log(message, Log.LevelEnum.INFO, correlationIDs);
    }

    public void warning(String message, UUID... correlationIDs){
        log(message, Log.LevelEnum.WARNING, correlationIDs);
    }

    public void error(String message, UUID... correlationIDs){
        log(message, Log.LevelEnum.ERROR, correlationIDs);
    }

    private void log(String message, Log.LevelEnum LogLevel, UUID... correlationIDs){
        Log log = new Log(UUID.randomUUID(), OffsetDateTime.now(), LogLevel, this.source, message).correlationIds(List.of(correlationIDs));
        try {
           String stringLog = mapper.writeValueAsString(log);
           LOG.debug("Sending message {} to: {}", stringLog, MessageRoutes.LOGGER_PERSIST_LOG);
            bus.talkAsync(exchangeName, MessageRoutes.LOGGER_PERSIST_LOG, stringLog);
        } catch (IOException e) {
           LOG.error(e.getMessage(), e);
        }
    }
}
