package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.CustomerDAO;
import ch.hslu.swda.dto.CustomerValidationRequest;
import ch.hslu.swda.dto.CustomerValidationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class CustomerValidationReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerValidationReceiver.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String exchangeName;
    private final BusConnector bus;
    private final CustomerDAO customerDAO;

    public CustomerValidationReceiver(final String exchangeName, final BusConnector bus, final CustomerDAO customerDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.customerDAO = customerDAO;
    }

    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {

        //log
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Begin message processing", threadName);
        LOG.debug("Received message with routing [{}]", route);
        LOG.debug("Received message: {}", message);

        try {
            CustomerValidationRequest request = MAPPER.readValue(message, CustomerValidationRequest.class);
            var isValid = isCustomerValid(request.getCustomerId());
            CustomerValidationResponse response = new CustomerValidationResponse(request.getCustomerId(), isValid);
            String data = MAPPER.writeValueAsString(response);
            LOG.debug("Sending response: {}", data);
            bus.reply(exchangeName, replyTo, corrId, data);
        } catch (JsonProcessingException e) {
            LOG.error("Could not process message. Cause: {}", e.getMessage(), e);
        } catch (IOException e) {
            LOG.error("Unable to communicate over bus. Cause: {}", e.getMessage(), e);
        }

    }

    private boolean isCustomerValid(final UUID customerId) {
       var response = customerDAO.findByUUID(customerId);
       return response != null;
    }
}
