package ch.hslu.swda.micro.inventory;

import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.entities.OrderInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class InventoryTakeItemsResponseReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryTakeItemsResponseReceiver.class);
    private final CompletableFuture<OrderInfo[]> future;

    public InventoryTakeItemsResponseReceiver(CompletableFuture<OrderInfo[]> future) {
        this.future = future;
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
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            OrderInfo[] response = mapper.readValue(message, OrderInfo[].class);
            future.complete(response);
        } catch (JsonProcessingException e) {
            LOG.error("Error while parsing take items response", e);
            future.completeExceptionally(new InventoryTakeItemsException("Error while parsing take items response", e));
        }
    }
}
