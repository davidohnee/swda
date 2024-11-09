package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(OrderMessageListener.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final OrdersMemory ordersMemory;
    private final List<UnvalidatedOrderListener> unvalidatedOrderListener;

    public OrderMessageListener(String exchangeName, BusConnector bus, OrdersMemory ordersMemory) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.ordersMemory = ordersMemory;
        this.unvalidatedOrderListener = new ArrayList<>();
    }

    public void addUnvalidatedOrderListener(UnvalidatedOrderListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        this.unvalidatedOrderListener.add(listener);
    }

    public void removeUnvalidatedOrderListener(UnvalidatedOrderListener listener) {
        this.unvalidatedOrderListener.remove(listener);
    }

    private void notifyUnvalidatedOrderListener(Order order) {
        for (UnvalidatedOrderListener listener : this.unvalidatedOrderListener) {
            listener.onUnvalidatedOrder(order);
        }
    }

    public void start() throws IOException {
        LOG.info("OrderService is now listening for messages...");
        this.bus.listenFor(
                this.exchangeName,
                "OrderService <- order.get.entityset",
                MessageRoutes.ORDER_GET_ENTITYSET,
                new GetOrderReceiver(this.exchangeName, this.bus, this.ordersMemory)
        );

        this.bus.listenFor(
                this.exchangeName,
                "OrderService <- order.get.entity",
                MessageRoutes.ORDER_GET_ENTITY,
                new GetOrderReceiver(this.exchangeName, this.bus, this.ordersMemory)
        );

        this.bus.listenFor(
                this.exchangeName,
                "OrderService <- order.create",
                MessageRoutes.ORDER_CREATE,
                new CreateOrderReceiver(
                        this.exchangeName,
                        this.bus,
                        this.ordersMemory,
                        this::notifyUnvalidatedOrderListener)
        );
    }
}
