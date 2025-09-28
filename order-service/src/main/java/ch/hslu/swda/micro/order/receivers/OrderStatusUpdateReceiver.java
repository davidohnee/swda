package ch.hslu.swda.micro.order.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.*;
import ch.hslu.swda.micro.Application;
import ch.hslu.swda.micro.inventory.InventoryServiceImpl;
import ch.hslu.swda.micro.inventory.receivers.InventoryCancelReplenishmentReceiver;
import ch.hslu.swda.micro.inventory.receivers.InventoryReturnResponseReceiver;
import ch.hslu.swda.micro.logging.LoggerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class OrderStatusUpdateReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStatusUpdateReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final PersistedOrderDAO persistedOrderDAO;
    private final OrderDAO orderDAO;
    private final ObjectMapper mapper;
    private final LoggerService loggerService;

    public OrderStatusUpdateReceiver(String exchangeName, BusConnector bus, PersistedOrderDAO persistedOrderDAO, OrderDAO orderDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.persistedOrderDAO = persistedOrderDAO;
        this.orderDAO = orderDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.loggerService = new LoggerService(Application.SERVICE_NAME, exchangeName, bus);
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
        LOG.info("Received message with routing [{}]", route);
        LOG.info("Received message: {}", message);
        try {
            OrderStatusUpdate orderStatusUpdate = this.mapper.readValue(message, OrderStatusUpdate.class);
            if (orderStatusUpdate.getStatus() == Order.StatusEnum.CANCELLED || orderStatusUpdate.getStatus() == Order.StatusEnum.SHIPPED) {
                LOG.info("Received order status update: {}", orderStatusUpdate);
                processOrderStatusUpdate(replyTo, corrId, orderStatusUpdate);
            } else {
                LOG.error("Only status CANCELLED and SHIPPED are supported");
                sendErrorResponse(replyTo, corrId, "Only status CANCELLED and SHIPPED are supported");
            }
        } catch (IOException e) {
            LOG.error("Error while processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private void processOrderStatusUpdate(String replyTo, String corrId, OrderStatusUpdate orderStatusUpdate) throws IOException {
        PersistedOrder persistedOrder = getPersistedOrder(orderStatusUpdate.getOrderId(), replyTo, corrId);
        if (persistedOrder == null) {
            return;
        }

        if (orderStatusUpdate.getStatus() == Order.StatusEnum.CANCELLED) {
            cancelOrder(replyTo, corrId, persistedOrder);
        } else if (orderStatusUpdate.getStatus() == Order.StatusEnum.SHIPPED) {
            updateOrderStatus(replyTo, corrId, persistedOrder, Order.StatusEnum.SHIPPED);
        }
    }

    private void cancelOrder(String replyTo, String corrId, PersistedOrder persistedOrder) throws IOException {
        if (persistedOrder.getStatus() == Order.StatusEnum.CANCELLED) {
            sendErrorResponse(replyTo, corrId, "Order is already cancelled");
            return;
        }

        updateOrderStatus(replyTo, corrId, persistedOrder, Order.StatusEnum.CANCELLED);
        notifyInventory(persistedOrder);
    }

    private void updateOrderStatus(String replyTo, String corrId, PersistedOrder persistedOrder, Order.StatusEnum newStatus) throws IOException {
        persistedOrder.setStatus(newStatus);
        this.persistedOrderDAO.update(persistedOrder.getId(), persistedOrder);
        this.loggerService.info("Order status updated to " + newStatus, persistedOrder.getOrderId());

        Order order = this.orderDAO.findByUUID(persistedOrder.getOrderId());
        sendResponse(replyTo, corrId, order);
    }

    private PersistedOrder getPersistedOrder(UUID orderId, String replyTo, String corrId) {
        PersistedOrder persistedOrder = this.persistedOrderDAO.findByUUID(orderId);
        LOG.info("Found order: {}", persistedOrder);

        if (persistedOrder == null) {
            LOG.error("Order not found: {}", orderId);
            sendErrorResponse(replyTo, corrId, "Order not found");
        }
        return persistedOrder;
    }

    private void notifyInventory(PersistedOrder persistedOrder) {
        cancelReplenishments(persistedOrder);
        returnItemsToInventory(persistedOrder);
    }

    private void cancelReplenishments(PersistedOrder persistedOrder) {
        persistedOrder.getOrderItems().stream()
                .filter(item -> item.getStatus() == OrderItemStatus.PENDING)
                .forEach(item -> {
                    this.loggerService.info("Cancelling replenishment for item " + item.getProductId(), persistedOrder.getOrderId());
                    MessageReceiver receiver = new InventoryCancelReplenishmentReceiver(persistedOrder, this.persistedOrderDAO);
                    new InventoryServiceImpl(this.bus, this.exchangeName).cancelReplenishment(item.getTrackingId(), receiver);
                });
    }

    private void returnItemsToInventory(PersistedOrder persistedOrder) {
        List<OrderItemCreate> list = persistedOrder.getOrderItems().stream()
                .map(item -> new OrderItemCreate(item.getProductId(), item.getQuantity()))
                .toList();
        MessageReceiver receiver = new InventoryReturnResponseReceiver(persistedOrder, this.persistedOrderDAO);
        new InventoryServiceImpl(this.bus, this.exchangeName).returnItems(list, receiver);
    }

    private void sendResponse(String replyTo, String corrId, Order order) throws IOException {
        String data = this.mapper.writeValueAsString(order);
        LOG.info("Sending response: {}", data);
        bus.reply(exchangeName, replyTo, corrId, data);
    }

    private void sendErrorResponse(String replyTo, String corrId, String errorMessage) {
        try {
            bus.reply(exchangeName, replyTo, corrId, errorMessage);
        } catch (IOException e) {
            LOG.error("Error sending error response", e);
        }
    }
}
