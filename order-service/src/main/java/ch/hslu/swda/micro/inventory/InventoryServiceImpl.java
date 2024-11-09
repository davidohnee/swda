package ch.hslu.swda.micro.inventory;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.dto.InventoryTakeItemsRequest;
import ch.hslu.swda.micro.MessageRoutes;
import ch.hslu.swda.model.OrderItemCreate;
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
    public CompletableFuture<Boolean> takeItems(List<OrderItemCreate> orderItems) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        LOG.debug("Taking items from inventory");
        String request = createTakeItemsRequest(new InventoryTakeItemsRequest(orderItems));
        return sendRequest(request, future);
    }

    private String createTakeItemsRequest(InventoryTakeItemsRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            LOG.error("Error while creating take items request", e);
            throw new InventoryTakeItemsException("Error while creating take items request", e);
        }
    }

    private CompletableFuture<Boolean> sendRequest(String request, CompletableFuture<Boolean> future) {
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
