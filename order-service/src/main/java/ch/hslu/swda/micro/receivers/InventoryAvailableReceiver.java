package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.Order;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemStatus;
import ch.hslu.swda.common.entities.PersistedOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryAvailableReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryAvailableReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final PersistedOrderDAO persistedOrderDAO;
    private final ObjectMapper mapper;

    public InventoryAvailableReceiver(String exchangeName, BusConnector bus, PersistedOrderDAO persistedOrderDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.persistedOrderDAO = persistedOrderDAO;
        this.mapper = new ObjectMapper();
    }

    private static void updateOrderStatus(PersistedOrder order) {
        long confirmedCount = order.getOrderItems().stream()
                .filter(item -> item.getStatus() == OrderItemStatus.DONE || item.getStatus() == OrderItemStatus.NOT_FOUND)
                .count();
        if (confirmedCount == order.getOrderItems().size()) {
            order.setStatus(Order.StatusEnum.CONFIRMED);
        }
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
        LOG.debug("Received message with routing [{}]", route);
        try {
            LOG.debug("Received message: {}", message);
            OrderInfo orderInfo = this.mapper.readValue(message, OrderInfo.class);
            updateOrder(orderInfo);
        } catch (JsonProcessingException e) {
            LOG.error("Error processing message", e);
        }
    }

    private void updateOrder(OrderInfo orderInfo) {
        PersistedOrder order = persistedOrderDAO.findByTrackingId(orderInfo.getTrackingId());
        if (order == null) {
            LOG.error("Order with trackingId: {} not found", orderInfo.getTrackingId());
            return;
        }
        for (OrderInfo item : order.getOrderItems()) {
            if (item.getProductId() == orderInfo.getProductId()) {
                item.setStatus(orderInfo.getStatus());
                item.setDeliveryDate(orderInfo.getDeliveryDate());
                break;
            }
        }
        updateOrderStatus(order);
        persistedOrderDAO.update(order.getId(), order);
        LOG.debug("Updated order with trackingId: {}", orderInfo.getTrackingId());
    }
}