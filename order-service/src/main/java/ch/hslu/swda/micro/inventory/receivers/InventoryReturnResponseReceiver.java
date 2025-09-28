package ch.hslu.swda.micro.inventory.receivers;

import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.OrderItemStatus;
import ch.hslu.swda.common.entities.PersistedOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryReturnResponseReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryReturnResponseReceiver.class);
    private final PersistedOrder persistedOrder;
    private final PersistedOrderDAO persistedOrderDAO;

    public InventoryReturnResponseReceiver(PersistedOrder persistedOrder, PersistedOrderDAO persistedOrderDAO) {
        this.persistedOrder = persistedOrder;
        this.persistedOrderDAO = persistedOrderDAO;
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
        this.persistedOrder.getOrderItems().forEach(orderItem -> {
            orderItem.setStatus(OrderItemStatus.RETURNED);
        });
        this.persistedOrderDAO.update(this.persistedOrder.getId(), this.persistedOrder);
    }
}
