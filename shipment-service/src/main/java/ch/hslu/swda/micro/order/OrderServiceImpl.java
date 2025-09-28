package ch.hslu.swda.micro.order;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.dto.OrderValidationRequest;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final BusConnector bus;
    private final String exchangeName;

    public OrderServiceImpl(BusConnector bus, String exchangeName) {
        this.bus = bus;
        this.exchangeName = exchangeName;
    }

    @Override
    public CompletableFuture<Boolean> validateOrder(UUID orderId) {
        CompletableFuture<Boolean> validationFuture = new CompletableFuture<>();
        LOG.info("Checking if order with id {} is in the correct state", orderId);
        String request = createCustomerValidateRequest(orderId);
        return sendRequest(request, validationFuture);
    }

    private String createCustomerValidateRequest(UUID orderId) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(new OrderValidationRequest(orderId));
        } catch (JsonProcessingException e) {
            LOG.error("Error while creating order validation request", e);
            throw new OrderValidateException("Error while creating order validation request", e);
        }
    }

    private CompletableFuture<Boolean> sendRequest(String request, CompletableFuture<Boolean> validationFuture) {
        try {
            bus.talkAsyncExperimental(
                this.exchangeName,
                MessageRoutes.SHIPMENT_VALIDATE,
                request,
                new OrderValidationResponseReceiver(validationFuture)
            );
        } catch (IOException | InterruptedException e) {
            LOG.error("Error while sending order validation request", e);
            validationFuture.completeExceptionally(new OrderValidateException("Failed to send validation request", e));
        }

        return validationFuture;
    }
}
