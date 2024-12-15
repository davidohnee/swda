package ch.hslu.swda.micro.customer.receiver;

import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.micro.customer.dto.CustomerValidationResponse;
import ch.hslu.swda.micro.customer.CustomerValidateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class CustomerValidationResponseReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerValidationResponseReceiver.class);
    private final CompletableFuture<Boolean> validationFuture;

    public CustomerValidationResponseReceiver(CompletableFuture<Boolean> validationFuture) {
        this.validationFuture = validationFuture;
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.info("Received message from customer service: {}", message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            CustomerValidationResponse response = mapper.readValue(message, CustomerValidationResponse.class);
            boolean isValid = response.getIsValid();
            validationFuture.complete(isValid);
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing customer validation response", e);
            validationFuture.completeExceptionally(new CustomerValidateException("Error parsing validation response", e));
        }
    }
}

