package ch.hslu.swda.common.entities;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class OrderInfo {
    private final UUID trackingId;
    private final int productId;
    private final int quantity;
    private OrderItemStatus status;
    private LocalDate deliveryDate;

    public OrderInfo(UUID trackingId, int productId, OrderItemStatus status, int quantity, LocalDate deliveryDate) {
        this.trackingId = trackingId;
        this.productId = productId;
        this.status = status;
        this.quantity = quantity;
        this.deliveryDate = deliveryDate;
    }

    /**
     * Constructor for OrderInfo, trackingId is generated
     *
     * @param productId    product id
     * @param status       status of the order
     * @param quantity     quantity of the product
     * @param deliveryDate delivery date
     */
    public OrderInfo(int productId, OrderItemStatus status, int quantity, LocalDate deliveryDate) {
        this(UUID.randomUUID(), productId, status, quantity, deliveryDate);
    }

    /**
     * Constructor for OrderInfo, trackingId is generated, delivery date is set to today
     *
     * @param productId product id
     * @param status    status of the order
     * @param quantity  quantity of the product
     */
    public OrderInfo(int productId, OrderItemStatus status, int quantity) {
        this(UUID.randomUUID(), productId, status, quantity, LocalDate.now());
    }

    public OrderInfo() {
        this(0, OrderItemStatus.NOT_FOUND, 0, LocalDate.now());
    }

    public UUID getTrackingId() {
        return trackingId;
    }

    public int getProductId() {
        return productId;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderInfo that)) return false;
        return productId == that.productId &&
                Objects.equals(trackingId, that.trackingId) &&
                status == that.status &&
                quantity == that.quantity &&
                Objects.equals(deliveryDate, that.deliveryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingId, productId, status, quantity, deliveryDate);
    }

    public String toString() {
        return "OrderInfo{" +
                "trackingId=" + trackingId +
                ", productId=" + productId +
                ", status=" + status +
                ", quantity=" + quantity +
                ", deliveryDate=" + deliveryDate +
                '}';
    }
}
