package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.Order;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemCreate;
import ch.hslu.swda.common.entities.PersistedOrder;
import ch.hslu.swda.micro.customer.CustomerService;
import ch.hslu.swda.micro.customer.CustomerServiceImpl;
import ch.hslu.swda.micro.customer.CustomerValidateException;
import ch.hslu.swda.micro.inventory.InventoryService;
import ch.hslu.swda.micro.inventory.InventoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OrderProcessingWorker {
    private static final int TIMEOUT_CUSTOMER_VALIDATION = 30;
    private static final int TIMEOUT_INVENTORY_RESERVATION = 30;
    private static final Logger LOG = LoggerFactory.getLogger(OrderProcessingWorker.class);
    private final PersistedOrderDAO persistedOrderDAO;
    private final CustomerService customerService;
    private final InventoryService inventoryService;

    public OrderProcessingWorker(String exchangeName, BusConnector bus, PersistedOrderDAO persistedOrderDAO) {
        this.persistedOrderDAO = persistedOrderDAO;
        this.customerService = new CustomerServiceImpl(bus, exchangeName);
        this.inventoryService = new InventoryServiceImpl(bus, exchangeName);
    }

    public void processOrder(PersistedOrder order) {
        UUID customerId = order.getCustomerId();

        customerService.validateCustomer(customerId)
                .orTimeout(TIMEOUT_CUSTOMER_VALIDATION, TimeUnit.SECONDS)  // Add a timeout of 5 seconds for customer validation
                .thenCompose(isValid -> {
                    if (!isValid) {
                        LOG.warn("Customer {} validation failed.", customerId);
                        return CompletableFuture.failedFuture(new CustomerValidateException("Customer validation failed"));
                    }
                    LOG.info("Customer {} validated. Proceeding with inventory reservation.", customerId);
                    List<OrderItemCreate> orderItems = order.getOrderItems().stream()
                            .map(orderItem -> new OrderItemCreate(orderItem.getProductId(), orderItem.getQuantity()))
                            .toList();
                    order.setStatus(Order.StatusEnum.PENDING);
                    LOG.info("Updating order {} status to PENDING.", order.getOrderId());
                    this.persistedOrderDAO.update(order.getId(), order);
                    return inventoryService.takeItems(orderItems)
                            .orTimeout(TIMEOUT_INVENTORY_RESERVATION, TimeUnit.SECONDS);  // Add a timeout of 10 seconds for inventory reservation
                })
                .thenAccept(orderInfos -> {
                    if (orderInfos != null) {
                        LOG.info("Inventory successfully reserved for order {}", order.getOrderId());
                        completeOrderProcessing(order, orderInfos);
                    } else {
                        LOG.warn("Inventory reservation failed for order {}", order.getOrderId());
                        handleOrderFailure(order);
                    }
                })
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof TimeoutException) {
                        LOG.error("Order processing timed out for order {}: {}", order.getOrderId(), ex.getMessage());
                    } else {
                        LOG.error("Order processing failed for order {}: {}", order.getOrderId(), ex.getMessage());
                    }
                    handleOrderFailure(order);
                    return null;
                });
    }


    private void completeOrderProcessing(PersistedOrder order, OrderInfo[] orderInfos) {
        order.setStatus(Order.StatusEnum.CONFIRMED);
        LOG.info("Updating order {} status to CONFIRMED.", order.getOrderId());
        order.setOrderItems(List.of(orderInfos));
        this.persistedOrderDAO.update(order.getId(), order);
        LOG.debug("Order {} updated in database.", order.getOrderId());
        LOG.info("Order {} processed successfully.", order.getOrderId());
    }

    private void handleOrderFailure(PersistedOrder order) {
        LOG.error("Failed to process order {}.", order.getId());
    }
}
