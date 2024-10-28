package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.db.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class CustomerService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);
    private String exchangeName;
    private BusConnector bus;
    private DBConnector db;

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
        this.receiveValidationRequest(); //asynchronous requests handler to validate Customers
        this.receiveGetRequest(); //synchronous request handler to get customers by API
    }

    private void receiveValidationRequest() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", CustomerRoutes.CUSTOMER_VALIDATE);
        bus.listenFor(exchangeName, "customer-validate-queue", CustomerRoutes.CUSTOMER_VALIDATE, new CustomerValidationReceiver(exchangeName, bus, db));
    }

    private void receiveGetRequest() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", CustomerRoutes.CUSTOMER_GET);
        bus.listenFor(exchangeName, "customer-get-queue", CustomerRoutes.CUSTOMER_GET, new CustomerGetReceiver(exchangeName, bus, db));
    }

    @Override
    public void close() throws Exception {
        bus.close();
        db.close();
    }
}
