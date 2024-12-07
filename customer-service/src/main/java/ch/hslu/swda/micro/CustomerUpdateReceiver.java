package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.CustomerDAO;
import ch.hslu.swda.common.entities.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomerUpdateReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerUpdateReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final CustomerDAO customerDAO;
    private final ObjectMapper mapper;


    public CustomerUpdateReceiver(String exchangeName, BusConnector bus, CustomerDAO customerDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.customerDAO = customerDAO;
        this.mapper = new ObjectMapper();
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
        LOG.info("Received message on route: {}", route);
        LOG.info("Received message: {}", message);
        try {
            Customer customer = this.mapper.readValue(message, Customer.class);
            LOG.info("Received customer: {}", customer);
            // implement update customer -> Customer doesnt have an ObjectId yet :(
        } catch(IOException e) {
            LOG.error("Error while parsing message: {}", e.getMessage());
        }
    }

}
