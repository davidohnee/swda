package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.entities.Customer;
import ch.hslu.swda.db.CustomerDBQuery;
import ch.hslu.swda.db.DBConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class CustomerValidationReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerGetReceiver.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String exchangeName;
    private final BusConnector bus;
    private final DBConnector db;

    public CustomerValidationReceiver(final String exchangeName, final BusConnector bus, final DBConnector db) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.db = db;
    }

    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {

        //log
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Begin message processing", threadName);
        LOG.debug("Received message with routing [{}]", route);

        //send asynchronously
        try {
            //deserialize msg
            Customer customer = deserialize(message);
            long id = customer.getId();

            //query the db
            CustomerDBQuery dbQuery = new CustomerDBQuery(db);
            ArrayList<Customer> customers = dbQuery.getCustomerById(id);

            //serialize msg
            String msg = serialize(customers);

            bus.talkAsync(exchangeName, "customer-async-talk", msg);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private Customer deserialize(final String msg) throws JsonProcessingException {
        Customer customer = MAPPER.readValue(msg, Customer.class);
        return customer;
    }

    private String serialize(final ArrayList<Customer> customers) throws JsonProcessingException {
        String msg = MAPPER.writeValueAsString(customers);
        return msg;
    }
}
