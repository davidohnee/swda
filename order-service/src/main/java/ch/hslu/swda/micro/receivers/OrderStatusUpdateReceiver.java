package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.database.OrderDAO;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.Order;
import ch.hslu.swda.common.entities.OrderStatusUpdate;
import ch.hslu.swda.common.entities.PersistedOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OrderStatusUpdateReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(OrderCustomerUpdateReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final PersistedOrderDAO persistedOrderDAO;
    private final OrderDAO orderDAO;
    private final ObjectMapper mapper;

    public OrderStatusUpdateReceiver(String exchangeName, BusConnector bus, PersistedOrderDAO persistedOrderDAO, OrderDAO orderDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.persistedOrderDAO = persistedOrderDAO;
        this.orderDAO = orderDAO;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
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
        LOG.info("Received message with routing [{}]", route);
        LOG.info("Received message: {}", message);
        try {
            OrderStatusUpdate orderStatusUpdate = this.mapper.readValue(message, OrderStatusUpdate.class);

            if (orderStatusUpdate.getStatus() != Order.StatusEnum.CANCELLED) {
                LOG.error("Only status CANCELLED is supported");
                sendErrorResponse(replyTo, corrId, "Only status CANCELLED is supported");
                return;
            }

            PersistedOrder persistedOrder = this.persistedOrderDAO.findByUUID(orderStatusUpdate.getOrderId());
            if (persistedOrder == null) {
                LOG.error("Order not found: {}", orderStatusUpdate.getOrderId());
                sendErrorResponse(replyTo, corrId, "Order not found");
            } else {
                persistedOrder.setStatus(orderStatusUpdate.getStatus());
                this.persistedOrderDAO.update(persistedOrder.getId(), persistedOrder);
                Order order = this.orderDAO.findByUUID(persistedOrder.getOrderId());
                sendResponse(replyTo, corrId, order);
            }
        } catch (IOException e) {
            LOG.error("Error while processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private void sendResponse(String replyTo, String corrId, Order order) throws IOException {
        String data = this.mapper.writeValueAsString(order);
        LOG.info("Sending response: {}", data);
        bus.reply(exchangeName, replyTo, corrId, data);
    }

    private void sendErrorResponse(String replyTo, String corrId, String errorMessage) {
        try {
            bus.reply(exchangeName, replyTo, corrId, errorMessage);
        } catch (IOException e) {
            LOG.error("Error sending error response", e);
        }
    }
}
