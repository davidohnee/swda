package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.database.LogDAO;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.micro.receivers.CreateLogReceiver;
import ch.hslu.swda.micro.receivers.GetLogReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LogMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(LogMessageListener.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final LogDAO logDAO;
    public LogMessageListener(String exchangeName, BusConnector bus, LogDAO logDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logDAO = logDAO;
    }
    public void start() throws IOException {
        LOG.info("LoggerService is now listening for messages...");
        this.bus.listenFor(
                this.exchangeName,
                "LoggerService <- logger.get.entityset",
                MessageRoutes.LOGGER_GET_ENTITYSET,
                new GetLogReceiver(this.exchangeName, this.bus, this.logDAO)
        );
        this.bus.listenFor(
                this.exchangeName,
                "LoggerService <- logger.get.entity",
                MessageRoutes.LOGGER_GET_ENTITY,
                new GetLogReceiver(this.exchangeName, this.bus, this.logDAO)
        );
        this.bus.listenFor(
                this.exchangeName,
                "LoggerService <- logger.log",
                MessageRoutes.LOGGER_PERSIST_LOG,
                new CreateLogReceiver(this.exchangeName, this.bus, this.logDAO)
        );
    }
}
