package ch.hslu.swda.micro.inventory;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemCreate;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.micro.inventory.dto.InventoryUpdateItemsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class InventoryServiceImpl implements InventoryService {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private final BusConnector bus;
    private final String exchangeName;
    private final ObjectMapper mapper;

    public InventoryServiceImpl(BusConnector bus, String exchangeName) {
        this.bus = bus;
        this.exchangeName = exchangeName;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public CompletableFuture<OrderInfo[]> takeItems(List<OrderItemCreate> orderItems) {
        CompletableFuture<OrderInfo[]> future = new CompletableFuture<>();
        String request = createTakeItemsRequest(new InventoryUpdateItemsRequest(orderItems));
        LOG.info("Taking items from inventory {}", request);
        return sendTakeItemsRequest(request, future);
    }

    /**
     * Takes an item from the inventory.
     *
     * @param orderItem The order item.
     * @param receiver  The message receiver.
     */
    @Override
    public void takeItem(OrderItemCreate orderItem, MessageReceiver receiver) {
        InventoryUpdateItemsRequest request = new InventoryUpdateItemsRequest(List.of(orderItem));
        try {
            String data = this.mapper.writeValueAsString(request);
            LOG.info("Sending take message to inventory: {}", data);
            this.bus.talkAsync(
                    this.exchangeName,
                    MessageRoutes.INVENTORY_TAKE,
                    data,
                    receiver
            );
        } catch (IOException | InterruptedException e) {
            LOG.error("Error sending take message to inventory", e);
        }
    }

    @Override
    public void returnItems(List<OrderItemCreate> orderItems, MessageReceiver receiver) {
        InventoryUpdateItemsRequest request = new InventoryUpdateItemsRequest(orderItems);
        try {
            String data = this.mapper.writeValueAsString(request);
            LOG.info("Sending return message to inventory: {}", data);
            this.bus.talkAsync(
                    this.exchangeName,
                    MessageRoutes.INVENTORY_ADD,
                    data,
                    receiver
            );
        } catch (IOException | InterruptedException e) {
            LOG.error("Error sending return message to inventory", e);
        }
    }

    /**
     * Returns an item to the inventory.
     *
     * @param orderItem The order item.
     * @param receiver  The message receiver.
     */
    @Override
    public void returnItem(OrderItemCreate orderItem, MessageReceiver receiver) {
        returnItems(List.of(orderItem), receiver);
    }

    /**
     * Cancels a replenishment.
     *
     * @param trackingId The tracking ID.
     * @param receiver   The message receiver.
     */
    @Override
    public void cancelReplenishment(UUID trackingId, MessageReceiver receiver) {
        try {
            String data = this.mapper.writeValueAsString(trackingId);
            LOG.info("Sending cancel replenishment message to inventory: {}", data);
            this.bus.talkAsync(
                    this.exchangeName,
                    MessageRoutes.INVENTORY_CANCEL,
                    data,
                    receiver
            );
        } catch (IOException | InterruptedException e) {
            LOG.error("Error sending cancel replenishment message to inventory", e);
        }
    }

    private String createTakeItemsRequest(InventoryUpdateItemsRequest request) {
        try {
            return this.mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            LOG.error("Error while creating take items request", e);
            throw new InventoryTakeItemsException("Error while creating take items request", e);
        }
    }

    private CompletableFuture<OrderInfo[]> sendTakeItemsRequest(String request, CompletableFuture<OrderInfo[]> future) {
        try {
            bus.talkAsync(
                    this.exchangeName,
                    MessageRoutes.INVENTORY_TAKE,
                    request,
                    new InventoryTakeItemsResponseReceiver(future)
            );
        } catch (IOException | InterruptedException e) {
            LOG.error("Error while sending take items request", e);
            future.completeExceptionally(new InventoryTakeItemsException("Error while sending take items request", e));
        }
        return future;
    }
}
