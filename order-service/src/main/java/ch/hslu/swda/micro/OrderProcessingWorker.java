package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.database.PersistedOrderDAO;
import ch.hslu.swda.common.entities.*;
import ch.hslu.swda.micro.customer.CustomerService;
import ch.hslu.swda.micro.customer.CustomerServiceImpl;
import ch.hslu.swda.micro.customer.CustomerValidateException;
import ch.hslu.swda.micro.inventory.InventoryService;
import ch.hslu.swda.micro.inventory.InventoryServiceImpl;
import ch.hslu.swda.micro.logging.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
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
    private final LoggerService loggerService;

    public OrderProcessingWorker(String exchangeName, BusConnector bus, PersistedOrderDAO persistedOrderDAO) {
        this.persistedOrderDAO = persistedOrderDAO;
        this.customerService = new CustomerServiceImpl(bus, exchangeName);
        this.inventoryService = new InventoryServiceImpl(bus, exchangeName);
        this.loggerService = new LoggerService(Application.SERVICE_NAME, exchangeName, bus);
    }

    public void processOrder(PersistedOrder order) {
        UUID customerId = order.getCustomerId();

        validateCustomer(customerId, order)
                .thenCompose(isValid -> reserveInventory(order))
                .thenAccept(orderInfos -> completeOrderProcessing(order, orderInfos))
                .exceptionally(ex -> {
                    handleProcessingException(order, ex);
                    return null;
                });
    }

    private CompletableFuture<Boolean> validateCustomer(UUID customerId, PersistedOrder order) {
        return customerService.validateCustomer(customerId)
                .orTimeout(TIMEOUT_CUSTOMER_VALIDATION, TimeUnit.SECONDS)
                .thenApply(isValid -> {
                    if (!isValid) {
                        LOG.warn("Customer {} validation failed.", customerId);
                        this.loggerService.warning("Customer validation failed", order.getOrderId(), customerId);
                        throw new CustomerValidateException("Customer validation failed");
                    }
                    LOG.info("Customer {} validated successfully.", customerId);
                    this.loggerService.info("Customer successfully validated", order.getOrderId(), customerId);
                    return true;
                });
    }

    private CompletableFuture<OrderInfo[]> reserveInventory(PersistedOrder order) {
        List<OrderItemCreate> orderItems = order.getOrderItems().stream()
                .map(item -> new OrderItemCreate(item.getProductId(), item.getQuantity()))
                .toList();

        updateOrderStatus(order, Order.StatusEnum.PENDING);

        return inventoryService.takeItems(orderItems)
                .orTimeout(TIMEOUT_INVENTORY_RESERVATION, TimeUnit.SECONDS)
                .thenCompose(orderInfos -> {
                    if (orderInfos == null) {
                        LOG.warn("Inventory reservation failed for order {}.", order.getOrderId());
                        this.loggerService.warning("Inventory reservation failed", order.getOrderId());
                        return CompletableFuture.failedFuture(new RuntimeException("Inventory reservation failed"));
                    }
                    LOG.info("Inventory items reserved successfully for order {}.", order.getOrderId());
                    this.loggerService.info("Inventory items reserved successfully", order.getOrderId());
                    return CompletableFuture.completedFuture(orderInfos);
                });
    }

    private void completeOrderProcessing(PersistedOrder order, OrderInfo[] orderInfos) {
        order.setOrderItems(List.of(orderInfos));
        updateOrderStatusAfterInventoryReserve(order, orderInfos);
        updateOrderPrice(order, orderInfos);
        persistedOrderDAO.update(order.getId(), order);
        LOG.info("Order {} processed successfully.", order.getOrderId());
    }

    private void updateOrderStatusAfterInventoryReserve(PersistedOrder order, OrderInfo[] orderInfos) {
        long confirmedCount = Arrays.stream(orderInfos)
                .filter(item -> item.getStatus() == OrderItemStatus.DONE || item.getStatus() == OrderItemStatus.NOT_FOUND)
                .count();
        if (confirmedCount == orderInfos.length) {
            updateOrderStatus(order, Order.StatusEnum.CONFIRMED);
        }
    }

    private void updateOrderPrice(PersistedOrder order, OrderInfo[] orderInfos) {
        BigDecimal totalPrice = Arrays.stream(orderInfos)
                .filter(item -> item.getStatus() != OrderItemStatus.NOT_FOUND)
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setPrice(totalPrice);
        LOG.info("Order {} total price updated to {}.", order.getOrderId(), totalPrice);
    }

    private void handleProcessingException(PersistedOrder order, Throwable ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof TimeoutException) {
            LOG.error("Processing timed out for order {}: {}", order.getOrderId(), cause.getMessage());
        } else if (cause instanceof CustomerValidateException) {
            LOG.error("Customer validation failed for order {}: {}", order.getOrderId(), cause.getMessage());
        } else {
            LOG.error("Processing failed for order {}: {}", order.getOrderId(), cause.getMessage());
        }
        handleOrderFailure(order);
    }

    private void updateOrderStatus(PersistedOrder order, Order.StatusEnum status) {
        order.setStatus(status);
        persistedOrderDAO.update(order.getId(), order);
        LOG.info("Order {} status updated to {}.", order.getOrderId(), status);
        this.loggerService.info("Order status updated to " + status, order.getOrderId());
    }

    private void handleOrderFailure(PersistedOrder order) {
        LOG.error("Order {} processing failed.", order.getOrderId());
    }
}
