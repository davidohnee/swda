package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.database.CustomerDAO;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.micro.receivers.CustomerCreateReceiver;
import ch.hslu.swda.micro.receivers.CustomerGetReceiver;
import ch.hslu.swda.micro.receivers.CustomerUpdateReceiver;
import ch.hslu.swda.micro.receivers.CustomerValidationReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomerMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerMessageListener.class);

    private final String exchangeName;
    private final BusConnector bus;
    private final CustomerDAO customerDAO;

    public CustomerMessageListener(String exchangeName, BusConnector bus, CustomerDAO customerDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.customerDAO = customerDAO;
    }

    public void start() throws IOException {
        LOG.info("CustomerService is now listening for messages...");

        this.bus.listenFor(
                this.exchangeName,
                "CustomerService <- customer.get.entityset",
                MessageRoutes.CUSTOMER_GET_ENTITYSET,
                new CustomerGetReceiver(this.exchangeName, this.bus, this.customerDAO)
        );

        this.bus.listenFor(
                this.exchangeName,
                "CustomerService <- customer.get.entity",
                MessageRoutes.CUSTOMER_GET_ENTITY,
                new CustomerGetReceiver(this.exchangeName, this.bus, this.customerDAO)
        );

        this.bus.listenFor(
                this.exchangeName,
                "CustomerService <- customer.create",
                MessageRoutes.CUSTOMER_CREATE,
                new CustomerCreateReceiver(
                        this.exchangeName,
                        this.bus,
                        this.customerDAO
                )
        );

        this.bus.listenFor(
                this.exchangeName,
                "CustomerService <- customer.update",
                MessageRoutes.CUSTOMER_UPDATE,
                new CustomerUpdateReceiver(
                        this.exchangeName,
                        this.bus,
                        this.customerDAO
                )
        );

        this.bus.listenFor(
                this.exchangeName,
                "CustomerService <- customer.validation",
                MessageRoutes.CUSTOMER_VALIDATE,
                new CustomerValidationReceiver(
                        this.exchangeName,
                        this.bus,
                        this.customerDAO
                )
        );
    }
}
