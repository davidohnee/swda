package ch.hslu.swda.models;

import java.util.Objects;
import java.util.UUID;

public class ReplenishTask {
    private final UUID trackingId;
    private final int productId;
    private final int count;
    private final ReplenishTaskReservation reservation;

    public ReplenishTask(
        int productId,
        int count,
        ReplenishTaskReservation reservation,
        UUID orderId
    ) {
        this.trackingId = orderId;
        this.productId = productId;
        this.count = count;
        this.reservation = reservation;
    }

    public ReplenishTask(
            int productId,
            int count,
            ReplenishTaskReservation reservation
    ) {
        this(productId, count, reservation, UUID.randomUUID());
    }

    public UUID getTrackingId() {
        return trackingId;
    }

    public ReplenishTaskReservation getReservation() {
        return reservation;
    }

    public int getCount() {
        return count;
    }

    public int getProductId() {
        return productId;
    }

    public boolean completed() {
        return count == this.reservation.getReservedCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplenishTask that)) return false;
        return productId == that.productId && count == that.count && Objects.equals(trackingId, that.trackingId) && Objects.equals(reservation, that.reservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingId, productId, count, reservation);
    }
}
