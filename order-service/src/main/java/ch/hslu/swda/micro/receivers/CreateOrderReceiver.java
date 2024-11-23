package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class CreateOrderReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CreateOrderReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final PersistedOrderDAO persistedOrderDAO;
    private final OrderDAO orderDAO;
    private final ObjectMapper mapper;
    private final Consumer<PersistedOrder> orderConsumer;

    public CreateOrderReceiver(
            String exchangeName,
            BusConnector bus,
            PersistedOrderDAO persistedOrderDAO,
            OrderDAO orderDAO,
            Consumer<PersistedOrder> orderConsumer
    ) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.persistedOrderDAO = persistedOrderDAO;
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
            PersistedOrder unvalidatedOrder = createUnvalidatedOrder(orderCreate);
            this.persistedOrderDAO.create(unvalidatedOrder);
            Order order = this.orderDAO.findByUUID(unvalidatedOrder.getOrderId());
            sendResponse(replyTo, corrId, order);
            orderConsumer.accept(unvalidatedOrder);
        } catch (IOException e) {
            LOG.error("Error processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private PersistedOrder createUnvalidatedOrder(OrderCreate orderCreate) {
        return new PersistedOrder(
                new ObjectId(),
                UUID.randomUUID(),
                orderCreate.getDateTime(),
                Order.StatusEnum.UNVALIDATED,
                createOrderInfos(orderCreate.getOrderItems()),
                BigDecimal.ZERO,
                Order.OrderTypeEnum.valueOf(orderCreate.getOrderType().name()),
                orderCreate.getCustomerId(),
                orderCreate.getSellerId(),
                orderCreate.getDestinationId()
        );
    }

    private void sendResponse(String replyTo, String corrId, Order order) throws IOException {
        String data = this.mapper.writeValueAsString(order);
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

    private List<OrderInfo> createOrderInfos(List<OrderItemCreate> orderItems) {
        return orderItems.stream().map(orderItemCreate -> new OrderInfo(
                null,
                orderItemCreate.getProductId(),
                OrderItemStatus.PENDING,
                orderItemCreate.getQuantity(),
                null
        )).toList();
    }
}
