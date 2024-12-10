package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.entities.Customer;
import ch.hslu.swda.common.database.CustomerDAO;
import ch.hslu.swda.common.entities.CustomerCreate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class CustomerCreateReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerCreateReceiver.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String exchangeName;
    private final BusConnector bus;
    private final CustomerDAO customerDAO;

    public CustomerCreateReceiver(final String exchangeName, final BusConnector bus, final CustomerDAO customerDAO) {
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

        try {
            var createCustomer = MAPPER.readValue(message, CustomerCreate.class);
            var customer = new Customer()
                    .id(new ObjectId())
                    .customerId(UUID.randomUUID())
                    .firstName(createCustomer.getFirstName())
                    .familyName(createCustomer.getFamilyName())
                    .address(createCustomer.getAddress())
                    .contactInfo(createCustomer.getContactInfo());
            customerDAO.create(customer);
            String response = new ObjectMapper().writeValueAsString(customer);
            bus.reply(exchangeName, replyTo, corrId, response);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to persist customer Cause: {}", e.getMessage(), e);
        } catch (IOException e) {
            LOG.error("Unable to communicate over bus. Cause: {}", e.getMessage(), e);
        }
    }
}
