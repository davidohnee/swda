package ch.hslu.swda.models;

import ch.hslu.swda.common.entities.ReplenishmentOrder;
import ch.hslu.swda.common.entities.SimpleReplenishmentOrder;
import ch.hslu.swda.entities.Product;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class ReplenishTask {
    private final UUID trackingId;
    private final int productId;
    private final int count;
    private final ReplenishTaskReservation reservation;
    private Product product;
    private final LocalDate deliveryDate;

    public ReplenishTask(
            int productId,
            int count,
            ReplenishTaskReservation reservation,
            UUID orderId,
            LocalDate deliveryDate
    ) {
        this.trackingId = orderId;
        this.productId = productId;
        this.count = count;
        this.reservation = reservation;
        this.deliveryDate = deliveryDate;
    }

    public ReplenishTask(
            int productId,
            int count,
            ReplenishTaskReservation reservation,
            LocalDate deliveryDate
    ) {
        this(productId, count, reservation, UUID.randomUUID(), deliveryDate);
    }

    public ReplenishTask(
            int productId,
            int count,
            LocalDate deliveryDate
    ) {
        this(productId, count, null, deliveryDate);
    }

    public static ReplenishTask fromSimpleReplenishmentOrder(SimpleReplenishmentOrder task) {
        return new ReplenishTask(
                task.getProductId(),
                task.getQuantity(),
                null,
                task.getTrackingId(),
                task.getDeliveryDate()
        );
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
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

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public boolean completed() {
        if (reservation == null) {
            return this.shouldHaveArrived();
        }
        return count == this.reservation.getReservedCount();
    }

    public boolean shouldHaveArrived() {
        return deliveryDate.isBefore(LocalDate.now());
    }

    public SimpleReplenishmentOrder toSimpleReplenishmentOrder() {
        return new SimpleReplenishmentOrder(
                this.trackingId,
                this.deliveryDate,
                ReplenishmentOrder.StatusEnum.CONFIRMED,
                this.productId,
                this.count
        );
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
