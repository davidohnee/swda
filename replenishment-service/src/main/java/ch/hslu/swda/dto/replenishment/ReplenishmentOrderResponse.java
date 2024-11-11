package ch.hslu.swda.dto.replenishment;

import ch.hslu.swda.entities.ReplenishmentStatus;

import java.util.Objects;
import java.util.UUID;

public class ReplenishmentOrderResponse {
    private final UUID trackingId;
    private final int productId;
    private final ReplenishmentStatus status;
    private final int quantity;

    public ReplenishmentOrderResponse(UUID trackingId, int productId, ReplenishmentStatus status, int quantity) {
        this.trackingId = trackingId;
        this.productId = productId;
        this.status = status;
        this.quantity = quantity;
    }

    public ReplenishmentOrderResponse(int productId, ReplenishmentStatus status, int quantity) {
        this(UUID.randomUUID(), productId, status, quantity);
    }

    public ReplenishmentOrderResponse() {
        this(0, ReplenishmentStatus.PENDING, 0);
    }

    public UUID getTrackingId() {
        return trackingId;
    }

    public int getProductId() {
        return productId;
    }

    public ReplenishmentStatus getStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplenishmentOrderResponse that)) return false;
        return productId == that.productId &&
                Objects.equals(trackingId, that.trackingId) &&
                status == that.status &&
                quantity == that.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingId, productId, status);
    }

    public String toString() {
        return "ReplenishmentOrderResponse{" +
                "trackingId=" + trackingId +
                ", productId=" + productId +
                ", status=" + status +
                ", quantity=" + quantity +
                '}';
    }
}
