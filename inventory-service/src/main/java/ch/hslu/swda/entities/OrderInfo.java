package ch.hslu.swda.entities;

import java.util.Objects;
import java.util.UUID;

public class OrderInfo {
    private final UUID trackingId;
    private final int productId;
    private final OrderItemStatus status;

    public OrderInfo(UUID trackingId, int productId, OrderItemStatus status) {
        this.trackingId = trackingId;
        this.productId = productId;
        this.status = status;
    }

    public OrderInfo(int productId, OrderItemStatus status) {
        this(UUID.randomUUID(), productId, status);
    }

    public OrderInfo() {
        this(0, OrderItemStatus.NOT_FOUND);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderInfo that)) return false;
        return productId == that.productId && Objects.equals(trackingId, that.trackingId) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingId, productId, status);
    }

    public String toString() {
        return "OrderInfo{" +
                "trackingId=" + trackingId +
                ", productId=" + productId +
                ", status=" + status +
                '}';
    }
}
