package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.db.CustomerDBQuery;
import ch.hslu.swda.db.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class CustomerService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);
    private static final String RECEIVER_START_MSG = "Starting listening for message with routing [{}]" ;
    private final String exchangeName;
    private final BusConnector bus;
    private final DBConnector db;

    public CustomerService() throws IOException, TimeoutException {
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        // connect to rmq
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();

        // connect to mongodb
        this.db = new DBConnector();
        this.db.connect();

        //start msg receiver
        this.receiveEntitysetRequest();
        this.receiveEntityRequest();
        this.receiveCreateRequest();
        this.receiveValidationRequest();
    }

    private void receiveEntitysetRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_GET_ENTITYSET);
        bus.listenFor(exchangeName, "CustomerService <- customer.get.entityset", MessageRoutes.CUSTOMER_GET_ENTITYSET, new CustomerGetReceiver(exchangeName, bus, new CustomerDBQuery(db)));
    }

    private void receiveEntityRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_GET_ENTITY);
        bus.listenFor(exchangeName, "CustomerService <- customer.get.entity", MessageRoutes.CUSTOMER_GET_ENTITY, new CustomerGetReceiver(exchangeName, bus, new CustomerDBQuery(db)));
    }

    private void receiveCreateRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_CREATE);
        bus.listenFor(exchangeName, "CustomerService <- customer.create", MessageRoutes.CUSTOMER_CREATE, new CustomerCreateReceiver(exchangeName, bus, new CustomerDBQuery(db)));
    }

    private void receiveValidationRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_VALIDATE);
        bus.listenFor(exchangeName, "CustomerService <- customer.validate", MessageRoutes.CUSTOMER_VALIDATE, new CustomerValidationReceiver(exchangeName, bus, new CustomerDBQuery(db)));
    }

    @Override
    public void close() throws Exception {
        bus.close();
        db.close();
    }
}
