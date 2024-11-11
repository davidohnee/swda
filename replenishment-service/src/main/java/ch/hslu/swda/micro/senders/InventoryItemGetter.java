package ch.hslu.swda.micro.senders;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.dto.inventory.InventoryItem;
import ch.hslu.swda.micro.receivers.CreateReplenishmentOrderReceiver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InventoryItemGetter implements InventoryClient {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryItemGetter.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final ObjectMapper mapper = new ObjectMapper();

    public InventoryItemGetter(String exchangeName, BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
    }

    @Override
    public void getInventoryItem(int productId, InventoryResponseHandler handler)
            throws IOException, InterruptedException {
        LOG.info("Requesting inventory item with id {}", productId);

        bus.talkAsync(
                exchangeName,
                MessageRoutes.INVENTORY_GET_ENTITY,
                String.valueOf(productId),
                (route, replyTo, corrId, message) -> {
                    InventoryItem item = null;

                    if (!message.equals("Product not found")) {
                        try {
                            item = mapper.readValue(message, InventoryItem.class);
                        } catch (IOException e) {
                            LOG.error("Error processing message", e);
                        }
                    }

                    LOG.info("Received inventory item: {}", item);

                    handler.handle(item);
                }
        );
    }
}
