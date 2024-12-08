package ch.hslu.swda.micro.inventory;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemCreate;
import ch.hslu.swda.dto.InventoryUpdateItemsRequest;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InventoryServiceImpl implements InventoryService {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private final BusConnector bus;
    private final String exchangeName;

    public InventoryServiceImpl(BusConnector bus, String exchangeName) {
        this.bus = bus;
        this.exchangeName = exchangeName;
    }

    @Override
    public CompletableFuture<OrderInfo[]> takeItems(List<OrderItemCreate> orderItems) {
        CompletableFuture<OrderInfo[]> future = new CompletableFuture<>();
        LOG.info("Taking items from inventory");
        String request = createTakeItemsRequest(new InventoryUpdateItemsRequest(orderItems));
        return sendRequest(request, future);
    }

    private String createTakeItemsRequest(InventoryUpdateItemsRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            LOG.error("Error while creating take items request", e);
            throw new InventoryTakeItemsException("Error while creating take items request", e);
        }
    }

    private CompletableFuture<OrderInfo[]> sendRequest(String request, CompletableFuture<OrderInfo[]> future) {
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
