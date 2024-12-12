package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.*;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.dto.InventoryUpdateItemsRequest;
import ch.hslu.swda.micro.inventory.InventoryTakeItemsResponseReceiver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OrderItemsUpdateReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(OrderItemsUpdateReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final PersistedOrderDAO persistedOrderDAO;
    private final OrderDAO orderDAO;
    private final ObjectMapper mapper;

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
            PersistedOrder persistedOrder = persistedOrderDAO.findByUUID(orderUpdate.getOrderId());
            updateOrderItems(persistedOrder, orderUpdate);
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

    private void updateOrderItems(PersistedOrder persistedOrder, OrderUpdate orderUpdate) {
        orderUpdate.getOrderItems().forEach(item -> {
            if (item.getQuantity() < 0) {
                throw new IllegalArgumentException("Quantity must not be negative");
            }
            int currentQuantity = getCurrentQuantity(persistedOrder, item);
            if (currentQuantity == -1) {
                addPersistedOrderItem(persistedOrder, item);
            } else {
                updatePersistedOrderItem(persistedOrder, item, currentQuantity);
            }
        });
    }

    private void updateOrderPrice(PersistedOrder persistedOrder) {
        BigDecimal totalPrice = persistedOrder.getOrderItems().stream()
                .filter(item -> item.getStatus() != OrderItemStatus.NOT_FOUND)
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        persistedOrder.setPrice(totalPrice);
        LOG.info("Order {} total price updated to {}.", persistedOrder.getOrderId(), totalPrice);
    }

    private static Integer getCurrentQuantity(PersistedOrder persistedOrder, OrderItemCreate item) {
        return persistedOrder.getOrderItems().stream()
                .filter(persistedItem -> persistedItem.getProductId() == item.getProductId())
                .findFirst()
                .map(OrderInfo::getQuantity)
                .orElse(-1);
    }

    private void updatePersistedOrderItem(PersistedOrder persistedOrder, OrderItemCreate item, int currentQuantity) {
        int quantityDifference = item.getQuantity() - currentQuantity;
        if (quantityDifference == 0) {
            return;
        } else if (quantityDifference > 0) {
            takeItemsFromInventory(item, quantityDifference);
        } else {
            returnItemsToInventory(item, -quantityDifference);
        }
        updateQuantity(persistedOrder, item);
        this.persistedOrderDAO.update(persistedOrder.getId(), persistedOrder);
        LOG.info("Updated order item: {}", item);
    }

    private static void updateQuantity(PersistedOrder persistedOrder, OrderItemCreate item) {
        persistedOrder.getOrderItems().stream()
                .filter(persistedItem -> persistedItem.getProductId() == item.getProductId())
                .findFirst()
                .ifPresent(persistedItem -> persistedItem.setQuantity(item.getQuantity()));
    }

    private void addPersistedOrderItem(PersistedOrder persistedOrder, OrderItemCreate item) {
        takeItemsFromInventory(item, item.getQuantity());
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setProductId(item.getProductId());
        orderInfo.setQuantity(item.getQuantity());
        persistedOrder.getOrderItems().add(orderInfo);
        this.persistedOrderDAO.update(persistedOrder.getId(), persistedOrder);
    }

    private void takeItemsFromInventory(OrderItemCreate item, int quantity) {
        OrderItemCreate updatedItem = new OrderItemCreate(item.getProductId(), quantity);
        InventoryUpdateItemsRequest request = new InventoryUpdateItemsRequest(List.of(updatedItem));
        try {
            String data = this.mapper.writeValueAsString(request);
            LOG.info("Taking items from inventory: {}", data);
            this.bus.talkAsync(
                    this.exchangeName,
                    MessageRoutes.INVENTORY_TAKE,
                    data,
                    new InventoryTakeItemsResponseReceiver(new CompletableFuture<>())
            );
        } catch (IOException | InterruptedException e) {
            LOG.error("Error taking items from inventory", e);
        }
    }

    private void returnItemsToInventory(OrderItemCreate item, int quantity) {
        OrderItemCreate updatedItem = new OrderItemCreate(item.getProductId(), quantity);
        InventoryUpdateItemsRequest request = new InventoryUpdateItemsRequest(List.of(updatedItem));
        try {
            String data = this.mapper.writeValueAsString(request);
            LOG.info("Sending return message to inventory: {}", data);
            this.bus.talkAsync(
                    this.exchangeName,
                    MessageRoutes.INVENTORY_ADD,
                    data,
                    new InventoryTakeItemsResponseReceiver(new CompletableFuture<>())
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
