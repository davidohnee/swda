package ch.hslu.swda.entities;

import java.util.Objects;
import java.util.UUID;

public class ReplenishmentOrderResponse {
    private final UUID trackingId;
    private final int productId;
    private final ReplenishmentStatus status;

    public ReplenishmentOrderResponse(UUID trackingId, int productId, ReplenishmentStatus status) {
        this.trackingId = trackingId;
        this.productId = productId;
        this.status = status;
    }

    public ReplenishmentOrderResponse(int productId, ReplenishmentStatus status) {
        this(UUID.randomUUID(), productId, status);
    }

    public ReplenishmentOrderResponse() {
        this(0, ReplenishmentStatus.PENDING);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplenishmentOrderResponse that)) return false;
        return productId == that.productId && Objects.equals(trackingId, that.trackingId) && status == that.status;
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
                '}';
    }
}
