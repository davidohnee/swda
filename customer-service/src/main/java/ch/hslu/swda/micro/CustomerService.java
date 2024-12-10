package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.database.CustomerDAO;
import ch.hslu.swda.common.database.MongoDBConnectionManager;
import ch.hslu.swda.common.config.ApplicationConfig;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.micro.receivers.CustomerCreateReceiver;
import ch.hslu.swda.micro.receivers.CustomerGetReceiver;
import ch.hslu.swda.micro.receivers.CustomerUpdateReceiver;
import ch.hslu.swda.micro.receivers.CustomerValidationReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class CustomerService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);
    private static final String RECEIVER_START_MSG = "Starting listening for message with routing [{}]" ;
    private final String exchangeName;
    private final BusConnector bus;
    private final MongoDBConnectionManager mongoDBConnectionManager;

    public CustomerService() throws IOException, TimeoutException {
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        // connect to rmq
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();

        // connect to mongodb
        this.mongoDBConnectionManager = MongoDBConnectionManager.getInstance(ApplicationConfig.getConnectionString(), ApplicationConfig.getDatabaseName());

        //start msg receiver
        this.receiveEntitysetRequest();
        this.receiveEntityRequest();
        this.receiveCreateRequest();
        this.receiveValidationRequest();
        this.receiveUpdateRequest();
    }

    private void receiveEntitysetRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_GET_ENTITYSET);
        bus.listenFor(exchangeName, "CustomerService <- customer.get.entityset", MessageRoutes.CUSTOMER_GET_ENTITYSET, new CustomerGetReceiver(exchangeName, bus, new CustomerDAO(mongoDBConnectionManager.getDatabase())));
    }

    private void receiveEntityRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_GET_ENTITY);
        bus.listenFor(exchangeName, "CustomerService <- customer.get.entity", MessageRoutes.CUSTOMER_GET_ENTITY, new CustomerGetReceiver(exchangeName, bus, new CustomerDAO(mongoDBConnectionManager.getDatabase())));
    }

    private void receiveCreateRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_CREATE);
        bus.listenFor(exchangeName, "CustomerService <- customer.create", MessageRoutes.CUSTOMER_CREATE, new CustomerCreateReceiver(exchangeName, bus, new CustomerDAO(mongoDBConnectionManager.getDatabase())));
    }

    private void receiveValidationRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_VALIDATE);
        bus.listenFor(exchangeName, "CustomerService <- customer.validate", MessageRoutes.CUSTOMER_VALIDATE, new CustomerValidationReceiver(exchangeName, bus, new CustomerDAO(mongoDBConnectionManager.getDatabase())));
    }

    private void receiveUpdateRequest() throws IOException {
        LOG.debug(RECEIVER_START_MSG, MessageRoutes.CUSTOMER_UPDATE);
        bus.listenFor(exchangeName, "CustomerService <- customer.update", MessageRoutes.CUSTOMER_UPDATE, new CustomerUpdateReceiver(exchangeName, bus, new CustomerDAO(mongoDBConnectionManager.getDatabase())));
    }

    @Override
    public void close() throws Exception {
        bus.close();
        mongoDBConnectionManager.close();
    }
}
