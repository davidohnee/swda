package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.db.CustomerDBQuery;
import ch.hslu.swda.entities.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class CustomerGetReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerGetReceiver.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String exchangeName;
    private final BusConnector bus;
    private final CustomerDBQuery dbQuery;

    public CustomerGetReceiver(final String exchangeName, final BusConnector bus, final CustomerDBQuery dbQuery) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.dbQuery = dbQuery;
    }

    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {

        //log
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Begin message processing", threadName);
        LOG.debug("Received message with routing [{}]", route);

        try {
            String msg = switch (route) {
                case MessageRoutes.CUSTOMER_GET_ENTITY -> {
                    var customerId = deserializeUUID(message);
                    Customer customer = dbQuery.getCustomerById(customerId);
                    yield (customer != null) ? MAPPER.writeValueAsString(customer) : null;
                }
                case MessageRoutes.CUSTOMER_GET_ENTITYSET -> {
                    var customers = dbQuery.getAllCustomer();
                    yield MAPPER.writeValueAsString(customers);
                }
                default -> {
                    LOG.warn("Unknown route: {}", route);
                    yield "Route unknown";
                }
            };

            LOG.debug("sending answer with topic [{}] according to replyTo-property", replyTo);
            bus.reply(exchangeName, replyTo, corrId, msg);
        } catch (JsonProcessingException e) {
            LOG.error("Could not process message. Cause: {}", e.getMessage(), e);
        } catch (IOException e) {
            LOG.error("Unable to communicate over bus. Cause: {}", e.getMessage(), e);
        }
    }

    private UUID deserializeUUID(final String msg) throws JsonProcessingException {
        return MAPPER.readValue(msg, UUID.class);
    }

    private String serializeCustomer(final Customer customer) throws JsonProcessingException {
        if (customer == null) {
            return "Customer not found";
        }
        return MAPPER.writeValueAsString(customer);
    }
}
