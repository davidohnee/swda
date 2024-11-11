package ch.hslu.swda.micro.senders;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.entities.OrderInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnItemAvailableSender implements OnItemAvailable {

    private static final Logger LOG = LoggerFactory.getLogger(OnItemAvailableSender.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final ObjectMapper mapper = new ObjectMapper();

    public OnItemAvailableSender(String exchangeName, BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
    }

    @Override
    public void onItemAvailable(OrderInfo orderInfo) {
        // send message
        try {
            String message = mapper.writeValueAsString(orderInfo);
            LOG.debug("sending replenished message");
            bus.talkAsync(exchangeName, MessageRoutes.INVENTORY_ON_AVAILABLE, message);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
