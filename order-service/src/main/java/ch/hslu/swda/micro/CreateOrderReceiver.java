package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class CreateOrderReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CreateOrderReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final OrderDAO orderDAO;
    private final ObjectMapper mapper;
    private final Consumer<Order> orderConsumer;

    public CreateOrderReceiver(String exchangeName, BusConnector bus, OrderDAO orderDAO, Consumer<Order> orderConsumer) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.orderDAO = orderDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.orderConsumer = orderConsumer;
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.debug("Received message with routing [{}]", route);
        try {
            LOG.debug("Received message: {}", message);
            OrderCreate orderCreate = this.mapper.readValue(message, OrderCreate.class);
            Order unvalidatedOrder = createUnvalidatedOrder(orderCreate);
            this.orderDAO.create(unvalidatedOrder);
            sendResponse(replyTo, corrId, unvalidatedOrder);
            orderConsumer.accept(unvalidatedOrder);
        } catch (IOException e) {
            LOG.error("Error processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private Order createUnvalidatedOrder(OrderCreate orderCreate) {
        return new Order(
                UUID.randomUUID(),
                orderCreate.getDateTime(),
                Order.StatusEnum.UNVALIDATED,
                getIncompleteOrderItems(orderCreate.getOrderItems()),
                null,
                Order.OrderTypeEnum.valueOf(orderCreate.getOrderType().name()),
                getIncompleteCustomer(orderCreate.getCustomerId()),
                getIncompleteEmployee(orderCreate.getSellerId()),
                getWarehouseById(orderCreate.getDestinationId())
        );
    }

    private void sendResponse(String replyTo, String corrId, Order unvalidatedOrder) throws IOException {
        String data = this.mapper.writeValueAsString(unvalidatedOrder);
        LOG.debug("Sending response: {}", data);
        bus.reply(exchangeName, replyTo, corrId, data);
    }

    private void sendErrorResponse(String replyTo, String corrId, String errorMessage) {
        try {
            bus.reply(exchangeName, replyTo, corrId, errorMessage);
        } catch (IOException e) {
            LOG.error("Error sending error response", e);
        }
    }

    private List<OrderItem> getIncompleteOrderItems(List<OrderItemCreate> items) {
        return items.stream()
                .map(itemCreate -> {
                    Product product = new Product(
                            itemCreate.getProductId(),
                            null,
                            null
                    );
                    return new OrderItem(product, itemCreate.getQuantity());
                })
                .toList();
    }

    private Customer getIncompleteCustomer(UUID customerId) {
        return new Customer(
                customerId,
                null,
                null,
                new Address(),
                new ContactInfo()
        );
    }

    private Employee getIncompleteEmployee(UUID employeeId) {
        return new Employee(
                employeeId,
                null,
                null,
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
