package ch.hslu.swda.entities;

import ch.hslu.swda.models.ReplenishTask;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class FullReplenishmentOrder {
    private final Product product;
    private final int quantity;
    private final ReplenishmentStatus status;
    private final UUID trackingId;
    private final LocalDate deliveryDate;

    public FullReplenishmentOrder(Product product, int quantity, ReplenishmentStatus status, UUID trackingId, LocalDate deliveryDate) {
        this.product = product;
        this.quantity = quantity;
        this.status = status;
        this.trackingId = trackingId;
        this.deliveryDate = deliveryDate;
    }

    public FullReplenishmentOrder() {
        this(new Product(), 0, ReplenishmentStatus.PENDING, UUID.randomUUID(), LocalDate.now());
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public ReplenishmentStatus getStatus() {
        return status;
    }

    public UUID getTrackingId() {
        return trackingId;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public static FullReplenishmentOrder fromReplenishTask(ReplenishTask task) {
        return new FullReplenishmentOrder(
                task.getProduct(),
                task.getCount(),
                task.completed() ? ReplenishmentStatus.DONE : ReplenishmentStatus.CONFIRMED,
                task.getTrackingId(),
                task.getDeliveryDate()
        );
    }

    public static List<FullReplenishmentOrder> fromReplenishTasks(List<ReplenishTask> tasks) {
        return tasks
                .stream()
                .map(FullReplenishmentOrder::fromReplenishTask)
                .toList();
    }
}
