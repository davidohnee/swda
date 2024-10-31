package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class CreateOrderReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CreateOrderReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final OrdersMemory ordersMemory;
    private final ObjectMapper mapper;

    public CreateOrderReceiver(String exchangeName, BusConnector bus, OrdersMemory ordersMemory) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.ordersMemory = ordersMemory;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.debug("Received message with routing [{}]", route);
        try {
            LOG.debug("Received message: {}", message);
            OrderCreate orderCreate = mapper.readValue(message, OrderCreate.class);

            Order order = new Order(
                    UUID.randomUUID(),
                    OffsetDateTime.now(),
                    Order.StatusEnum.PENDING,
                    getOrderItems(orderCreate.getOrderItems()),
                    new BigDecimal(100),
                    Order.OrderTypeEnum.CUSTOMER_ORDER,
                    getCustomerById(orderCreate.getCustomerId()),
                    getEmployeeById(orderCreate.getSellerId()),
                    getWarehouseById(orderCreate.getDestinationId())
            );

            ordersMemory.addOrder(order);
            String data = mapper.writeValueAsString(order);

            LOG.debug("Sending response: {}", data);
            bus.reply(exchangeName, replyTo, corrId, data);
        } catch (IOException e) {
            LOG.error("Error processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private void sendErrorResponse(String replyTo, String corrId, String errorMessage) {
        try {
            bus.reply(exchangeName, replyTo, corrId, errorMessage);
        } catch (IOException e) {
            LOG.error("Error sending error response", e);
        }
    }

    private List<OrderItem> getOrderItems(List<OrderItemCreate> items) {
        // TODO: Implement method to retrieve OrderItems by Order ID
        // For now, return a dummy list of OrderItems based on the OrderItemCreate list
        List<String> productNames = List.of(
                "Widget",
                "Gadget",
                "Thingamajig",
                "Doohickey",
                "Contraption",
                "Gizmo",
                "Apparatus",
                "Instrument",
                "Device",
                "Mechanism"
        );

        ThreadLocalRandom random = ThreadLocalRandom.current();

        return items.stream()
                .map(itemCreate -> {
                    String productName = productNames.get(random.nextInt(productNames.size()));

                    BigDecimal price = BigDecimal.valueOf(random.nextDouble(10.0, 500.0))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);

                    Product product = new Product(
                            itemCreate.getProductId(),
                            productName,
                            price
                    );

                    return new OrderItem(product, itemCreate.getQuantity());
                })
                .collect(Collectors.toList());
    }

    private Customer getCustomerById(UUID customerId) {
        // TODO: Implement method to retrieve Customer by ID
        // For now, return a dummy customer
        return new Customer(
                customerId,
                "FirstName",
                "LastName",
                new Address("Street", "Number", "PostalCode", "City"),
                new ContactInfo()
        );
    }

    private Employee getEmployeeById(UUID employeeId) {
        // TODO: Implement method to retrieve Employee by ID
        // For now, return a dummy employee
        return new Employee(
                employeeId,
                "FirstName",
                "LastName",
                Employee.RoleEnum.SALES
        );
    }

    private Warehouse getWarehouseById(UUID warehouseId) {
        // TODO: Implement method to retrieve Warehouse by ID
        // For now, return a dummy warehouse
        return new Warehouse(
                warehouseId,
                Warehouse.TypeEnum.LOCAL
        );
    }
}
