package ch.hslu.swda.micro.order;

import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.dto.OrderValidationRequest;
import ch.hslu.swda.dto.OrderValidationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class OrderValidationResponseReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(OrderValidationResponseReceiver.class);
    private final CompletableFuture<Boolean> validationFuture;

    public OrderValidationResponseReceiver(CompletableFuture<Boolean> validationFuture) {
        this.validationFuture = validationFuture;
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.info("Received message from customer service: {}", message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            OrderValidationResponse response = mapper.readValue(message, OrderValidationResponse.class);
            boolean isValid = response.getIsValid();
            validationFuture.complete(isValid);
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing customer validation response", e);
            validationFuture.completeExceptionally(new OrderValidateException("Error parsing validation response", e));
        }
    }
}

