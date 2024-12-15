package ch.hslu.swda.micro.inventory.receivers;

import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.PersistedOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryTakeItemResponseReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryTakeItemResponseReceiver.class);
    private final PersistedOrder persistedOrder;
    private final PersistedOrderDAO persistedOrderDAO;
    private final ObjectMapper mapper;

    public InventoryTakeItemResponseReceiver(PersistedOrder persistedOrder, PersistedOrderDAO persistedOrderDAO) {
        this.persistedOrder = persistedOrder;
        this.persistedOrderDAO = persistedOrderDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    /**
     * Listener Methode für Messages.
     *
     * @param route   Route.
     * @param replyTo ReplyTo Route.
     * @param corrId  corrId.
     * @param message Message.
     */
    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.info("Received message from inventory service: {}", message);
        try {
            OrderInfo[] response = this.mapper.readValue(message, OrderInfo[].class);
            updateOrder(response);
        } catch (JsonProcessingException e) {
            LOG.error("Error while parsing take items response", e);
        }
    }

    private void updateOrder(OrderInfo[] orderInfo) {
        for (OrderInfo item : orderInfo) {
            this.persistedOrder.getOrderItems().stream()
                    .filter(orderItem -> orderItem.getProductId() == item.getProductId())
                    .findFirst()
                    .ifPresent(orderItem -> {
                        orderItem.setStatus(item.getStatus());
                        orderItem.setTrackingId(item.getTrackingId());
                        orderItem.setDeliveryDate(item.getDeliveryDate());
                    });
        }
        this.persistedOrderDAO.update(this.persistedOrder.getId(), this.persistedOrder);
    }
}
