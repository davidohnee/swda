package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public final class OrderService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final OrdersMemory ordersMemory;

    public OrderService() throws IOException, TimeoutException {
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();

        this.ordersMemory = new OrdersMemory();
    }

    public void readOrder() throws IOException {
        LOG.debug("Listening for messages on routing [{}]", MessageRoutes.ORDER_GET_ENTITYSET);
        bus.listenFor(
                exchangeName,
                "OrderService <- order.get.entityset",
                MessageRoutes.ORDER_GET_ENTITYSET,
                new GetOrderReceiver(this.exchangeName, this.bus, this.ordersMemory)
        );

        LOG.debug("Listening for messages on routing [{}]", MessageRoutes.ORDER_GET_ENTITY);
        bus.listenFor(
                exchangeName,
                "OrderService <- order.get.entity",
                MessageRoutes.ORDER_GET_ENTITY,
                new GetOrderReceiver(this.exchangeName, this.bus, this.ordersMemory)
        );
    }

    // only for testing purposes
    public void createOrder() throws IOException {
        Order order = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now().minusDays(7),
                Order.StatusEnum.PENDING,
                Collections.emptyList(),
                new BigDecimal(1200),
                Order.OrderTypeEnum.CUSTOMER_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "Kelly",
                        "Clark",
                        new Address("Seventh Street", "7D", "8901", "Pinewood"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Leo",
                        "Red",
                        Employee.RoleEnum.MANAGER
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.LOCAL
                )
        );
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        String data = mapper.writeValueAsString(order);

        LOG.debug("Sending asynchronous message to broker with routing [{}]", MessageRoutes.ORDER_CREATE);
        bus.talkAsync(this.exchangeName, MessageRoutes.ORDER_CREATE, data);
    }


    @Override
    public void close() {
        bus.close();
    }
}