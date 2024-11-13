package ch.hslu.swda.common.entities;

import java.util.Objects;
import java.util.UUID;

public class OrderInfo {
    private final UUID trackingId;
    private final int productId;
    private final OrderItemStatus status;
    private final int quantity;

    public OrderInfo(UUID trackingId, int productId, OrderItemStatus status, int quantity) {
        this.trackingId = trackingId;
        this.productId = productId;
        this.status = status;
        this.quantity = quantity;
    }

    public OrderInfo(int productId, OrderItemStatus status, int quantity) {
        this(UUID.randomUUID(), productId, status, quantity);
    }

    public OrderInfo() {
        this(0, OrderItemStatus.NOT_FOUND, 0);
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

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderInfo that)) return false;
        return productId == that.productId &&
                Objects.equals(trackingId, that.trackingId) &&
                status == that.status &&
                quantity == that.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingId, productId, status, quantity);
    }

    public String toString() {
        return "OrderInfo{" +
                "trackingId=" + trackingId +
                ", productId=" + productId +
                ", status=" + status +
                ", quantity=" + quantity +
                '}';
    }
}
