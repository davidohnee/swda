package ch.hslu.swda.micro.senders;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.dto.replenishment.ReplenishmentOrder;
import ch.hslu.swda.entities.ReplenishResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ReplenishmentClientSender implements ReplenishmentClient {
    private static final Logger LOG = LoggerFactory.getLogger(ReplenishmentClientSender.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReplenishmentClientSender(String exchangeName, BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;

        this.mapper.registerModule(new JavaTimeModule());
    }

    public void replenish(
            ReplenishmentOrder order,
            ReplenishResponseHandler handler
    ) throws IOException, InterruptedException {
        final String message = mapper.writeValueAsString(order);

        LOG.debug("Sending synchronous message to broker with routing [{}]", MessageRoutes.REPLENISHMENT_CREATE);
        bus.talkAsync(exchangeName, MessageRoutes.REPLENISHMENT_CREATE, message,
                (final String route, final String replyTo, final String corrId, final String response) -> {
                    LOG.debug("Received response with routing [{}]", route);
                    OrderInfo request = mapper.readValue(response, OrderInfo.class);
                    handler.handle(request);
                });
    }
}
