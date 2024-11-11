package ch.hslu.swda.micro.senders;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.dto.replenishment.ReplenishmentOrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnItemReplenishedSender implements OnItemReplenished {

    private static final Logger LOG = LoggerFactory.getLogger(OnItemReplenishedSender.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final ObjectMapper mapper = new ObjectMapper();

    public OnItemReplenishedSender(String exchangeName, BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
    }

    @Override
    public void onItemReplenished(ReplenishmentOrderResponse response) {
        // send message
        try {
            String message = mapper.writeValueAsString(response);
            LOG.debug("sending replenished message");
            bus.talkAsync(exchangeName, MessageRoutes.REPLENISHMENT_ON_REPLENISH, message);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
