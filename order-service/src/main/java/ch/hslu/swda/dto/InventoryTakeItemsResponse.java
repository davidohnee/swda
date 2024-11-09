package ch.hslu.swda.dto;

import java.util.Objects;
import java.util.UUID;

public final class InventoryTakeItemsResponse {
    private final UUID trackingId;
    private final int productId;
    private final OrderItemStatus status;

    public InventoryTakeItemsResponse(UUID trackingId, int productId, OrderItemStatus status) {
        this.trackingId = trackingId;
        this.productId = productId;
        this.status = status;
    }

    public InventoryTakeItemsResponse(int productId, OrderItemStatus status) {
        this(UUID.randomUUID(), productId, status);
    }

    public InventoryTakeItemsResponse() {
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
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof InventoryTakeItemsResponse that)
                &&
                (that.productId == this.productId)
                && Objects.equals(that.trackingId, this.trackingId)
                && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingId, productId, status);
    }

    @Override
    public String toString() {
        return "InventoryTakeItemsResponse [" +
                "trackingId=" + trackingId +
                ", productId=" + productId +
                ", status=" + status +
                ']';
    }
}
