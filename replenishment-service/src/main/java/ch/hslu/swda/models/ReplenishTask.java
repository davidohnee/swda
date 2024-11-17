package ch.hslu.swda.models;

import ch.hslu.swda.common.entities.ReplenishmentOrder;
import ch.hslu.swda.common.entities.ReplenishmentReservation;
import ch.hslu.swda.common.entities.SimpleReplenishmentOrder;
import ch.hslu.swda.entities.Product;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class ReplenishTask {
    private final UUID trackingId;
    private final int productId;
    private final int count;
    private ReplenishmentReservation reservation;
    private Product product;
    private LocalDate deliveryDate;
    private ObjectId id;

    public ReplenishTask(
            int productId,
            int count,
            ReplenishmentReservation reservation,
            UUID orderId,
            LocalDate deliveryDate, ObjectId id
    ) {
        this.trackingId = orderId;
        this.productId = productId;
        this.count = count;
        this.reservation = reservation;
        this.deliveryDate = deliveryDate;
        this.id = id;
    }

    public ReplenishTask(
            int productId,
            int count,
            ReplenishmentReservation reservation,
            LocalDate deliveryDate
    ) {
        this(productId, count, reservation, UUID.randomUUID(), deliveryDate, new ObjectId());
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
                task.getReservation(),
                task.getTrackingId(),
                task.getDeliveryDate(),
                task.getId()
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

    public ReplenishmentReservation getReservation() {
        return reservation;
    }

    public void setReservation(ReplenishmentReservation reservation) {
        this.reservation = reservation;
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

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public ObjectId getId() {
        return id;
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
                this.count,
                this.reservation,
                this.id
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

    @Override
    public String toString() {
        return "ReplenishTask [" +
                "trackingId='" + this.trackingId + '\'' +
                ", productId='" + this.productId + '\'' +
                ", count='" + this.count + '\'' +
                ", reservation='" + this.reservation + '\'' +
                ", deliveryDate='" + this.deliveryDate + '\'' +
                ']';
    }
}
