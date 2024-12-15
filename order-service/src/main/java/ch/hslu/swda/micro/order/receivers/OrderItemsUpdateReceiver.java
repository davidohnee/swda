package ch.hslu.swda.micro.order.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.*;
import ch.hslu.swda.micro.inventory.InventoryServiceImpl;
import ch.hslu.swda.micro.inventory.receivers.InventoryTakeItemResponseReceiver;
import ch.hslu.swda.micro.inventory.receivers.InventoryReturnResponseReceiver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

public class OrderItemsUpdateReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(OrderItemsUpdateReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final PersistedOrderDAO persistedOrderDAO;
    private final OrderDAO orderDAO;
    private final ObjectMapper mapper;
    private PersistedOrder persistedOrder;

    public OrderItemsUpdateReceiver(String exchangeName, BusConnector bus, PersistedOrderDAO persistedOrderDAO, OrderDAO orderDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.persistedOrderDAO = persistedOrderDAO;
        this.orderDAO = orderDAO;
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
        LOG.info("Received message with routing [{}]", route);
        LOG.info("Received message: {}", message);
        try {
            OrderUpdate orderUpdate = mapper.readValue(message, OrderUpdate.class);
            this.persistedOrder = persistedOrderDAO.findByUUID(orderUpdate.getOrderId());
            if (this.persistedOrder == null) {
                sendErrorResponse(replyTo, corrId, "Order not found");
                return;
            }

            updateOrderItems(orderUpdate);
            updateOrderPrice(persistedOrder);

            this.persistedOrderDAO.update(persistedOrder.getId(), persistedOrder);
            Order order = orderDAO.findByUUID(persistedOrder.getOrderId());
            sendResponse(replyTo, corrId, order);
        } catch (IOException e) {
            LOG.error("Error while processing message", e);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(replyTo, corrId, e.getMessage());
        }
    }

    private void updateOrderItems(OrderUpdate orderUpdate) {
        orderUpdate.getOrderItems().forEach(item -> {
            if (item.getQuantity() < 0) {
                throw new IllegalArgumentException("Quantity must not be negative");
            }
            int currentQuantity = getCurrentQuantity(item);
            if (currentQuantity == -1) {
                addNewOrderItem(item);
            } else {
                updateExistingOrderItem(item, currentQuantity);
            }
        });
    }

    private void addNewOrderItem(OrderItemCreate item) {
        takeItemFromInventory(item);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setProductId(item.getProductId());
        orderInfo.setQuantity(item.getQuantity());
        this.persistedOrder.getOrderItems().add(orderInfo);
        this.persistedOrderDAO.update(this.persistedOrder.getId(), this.persistedOrder);
    }

    private void updateExistingOrderItem(OrderItemCreate item, int currentQuantity) {
        int quantityDifference = item.getQuantity() - currentQuantity;
        if (quantityDifference == 0) {
            return;
        } else if (quantityDifference > 0) {
            OrderItemCreate additional = new OrderItemCreate(item.getProductId(), quantityDifference);
            takeItemFromInventory(additional);
        } else {
            OrderItemCreate surplus = new OrderItemCreate(item.getProductId(), -quantityDifference);
            returnItemToInventory(surplus);
        }
        updateQuantity(item);
        this.persistedOrderDAO.update(this.persistedOrder.getId(), this.persistedOrder);
        LOG.info("Updated order item: {}", item);
    }

    private void updateQuantity(OrderItemCreate item) {
        this.persistedOrder.getOrderItems().stream()
                .filter(persistedItem -> persistedItem.getProductId() == item.getProductId())
                .findFirst()
                .ifPresent(persistedItem -> persistedItem.setQuantity(item.getQuantity()));
    }

    private void updateOrderPrice(PersistedOrder persistedOrder) {
        BigDecimal totalPrice = persistedOrder.getOrderItems().stream()
                .filter(item -> item.getStatus() != OrderItemStatus.NOT_FOUND)
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        persistedOrder.setPrice(totalPrice);
        LOG.info("Order {} total price updated to {}.", persistedOrder.getOrderId(), totalPrice);
    }

    private Integer getCurrentQuantity(OrderItemCreate item) {
        return this.persistedOrder.getOrderItems().stream()
                .filter(persistedItem -> persistedItem.getProductId() == item.getProductId())
                .findFirst()
                .map(OrderInfo::getQuantity)
                .orElse(-1);
    }

    private void takeItemFromInventory(OrderItemCreate item) {
        new InventoryServiceImpl(this.bus, this.exchangeName)
                .takeItem(item, new InventoryTakeItemResponseReceiver(this.persistedOrder, this.persistedOrderDAO));
    }

    private void returnItemToInventory(OrderItemCreate item) {
        new InventoryServiceImpl(this.bus, this.exchangeName)
                .returnItem(item, new InventoryReturnResponseReceiver(this.persistedOrder, this.persistedOrderDAO));
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
