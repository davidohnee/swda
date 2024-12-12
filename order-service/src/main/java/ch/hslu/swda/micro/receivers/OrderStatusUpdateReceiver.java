package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.Order;
import ch.hslu.swda.common.entities.OrderItemCreate;
import ch.hslu.swda.common.entities.OrderStatusUpdate;
import ch.hslu.swda.common.entities.PersistedOrder;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.dto.InventoryUpdateItemsRequest;
import ch.hslu.swda.micro.Application;
import ch.hslu.swda.micro.logging.LoggerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

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
            if (orderStatusUpdate.getStatus() != Order.StatusEnum.CANCELLED) {
                LOG.error("Only status CANCELLED is supported");
                sendErrorResponse(replyTo, corrId, "Only status CANCELLED is supported");
                return;
            }
            cancelOrder(replyTo, corrId, orderStatusUpdate);
        } catch (IOException e) {
            LOG.error("Error while processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private void cancelOrder(String replyTo, String corrId, OrderStatusUpdate orderStatusUpdate) throws IOException {
        PersistedOrder persistedOrder = this.persistedOrderDAO.findByUUID(orderStatusUpdate.getOrderId());
        LOG.info("Found order: {}", persistedOrder);
        if (persistedOrder == null) {
            LOG.error("Order not found: {}", orderStatusUpdate.getOrderId());
            sendErrorResponse(replyTo, corrId, "Order not found");
            return;
        }

        if (persistedOrder.getStatus() == Order.StatusEnum.CANCELLED) {
            LOG.error("Order is already cancelled: {}", orderStatusUpdate.getOrderId());
            sendErrorResponse(replyTo, corrId, "Order is already cancelled");
            return;
        }

        persistedOrder.setStatus(Order.StatusEnum.CANCELLED);
        this.persistedOrderDAO.update(persistedOrder.getId(), persistedOrder);
        this.loggerService.info("Order status updated to CANCELLED", persistedOrder.getOrderId());
        Order order = this.orderDAO.findByUUID(persistedOrder.getOrderId());
        sendResponse(replyTo, corrId, order);
        notifyInventory(persistedOrder);
    }

    private void notifyInventory(PersistedOrder persistedOrder) {
        cancelReplenishments(persistedOrder);
        returnItemsToInventory(persistedOrder);
    }

    private void cancelReplenishments(PersistedOrder persistedOrder) {
        persistedOrder.getOrderItems().stream()
                .map(item -> {
                    try {
                        return this.mapper.writeValueAsString(item.getTrackingId());
                    } catch (JsonProcessingException e) {
                        LOG.error("Error serializing trackingId", e);
                        return null;
                    }
                })
                .forEach(trackingId -> {
                    try {
                        if (trackingId != null) {
                            LOG.info("Sending cancel message to inventory for trackingId {}", trackingId);
                            this.bus.talkAsync(
                                    this.exchangeName,
                                    MessageRoutes.INVENTORY_CANCEL,
                                    trackingId,
                                    new CancelInventoryItemReceiver(persistedOrder, this.persistedOrderDAO));
                        }
                    } catch (IOException | InterruptedException e) {
                        LOG.error("Error sending cancel message to inventory", e);
                    }
                });
    }

    private void returnItemsToInventory(PersistedOrder persistedOrder) {
        List<OrderItemCreate> list = persistedOrder.getOrderItems().stream()
                .map(item -> new OrderItemCreate(item.getProductId(), item.getQuantity()))
                .toList();
        InventoryUpdateItemsRequest request = new InventoryUpdateItemsRequest(list);
        try {
            String data = this.mapper.writeValueAsString(request);
            LOG.info("Sending return message to inventory: {}", data);
            this.bus.talkAsync(
                    this.exchangeName,
                    MessageRoutes.INVENTORY_ADD,
                    data,
                    new ReturnInventoryItemsReceiver(persistedOrder, this.persistedOrderDAO)
            );
        } catch (IOException | InterruptedException e) {
            LOG.error("Error sending return message to inventory", e);
        }
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
