package ch.hslu.swda.micro.customer;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.dto.CustomerValidationRequest;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CustomerServiceImpl implements CustomerService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final BusConnector bus;
    private final String exchangeName;

    public CustomerServiceImpl(BusConnector bus, String exchangeName) {
        this.bus = bus;
        this.exchangeName = exchangeName;
    }

    @Override
    public CompletableFuture<Boolean> validateCustomer(UUID customerId) {
        CompletableFuture<Boolean> validationFuture = new CompletableFuture<>();
        LOG.info("Checking if customer with id {} is valid", customerId);
        String request = createCustomerValidateRequest(customerId);
        return sendRequest(request, validationFuture);
    }

    private String createCustomerValidateRequest(UUID customerId) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(new CustomerValidationRequest(customerId));
        } catch (JsonProcessingException e) {
            LOG.error("Error while creating customer validation request", e);
            throw new CustomerValidateException("Error while creating customer validation request", e);
        }
    }

    private CompletableFuture<Boolean> sendRequest(String request, CompletableFuture<Boolean> validationFuture) {
        try {
            bus.talkAsync(
                    this.exchangeName,
                    MessageRoutes.CUSTOMER_VALIDATE,
                    request,
                    new CustomerValidationResponseReceiver(validationFuture)
            );
        } catch (IOException | InterruptedException e) {
            LOG.error("Error while sending customer validation request", e);
            validationFuture.completeExceptionally(new CustomerValidateException("Failed to send validation request", e));
        }

        return validationFuture;
    }
}
