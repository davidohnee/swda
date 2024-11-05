package ch.hslu.swda.models;

import java.time.Instant;
import java.time.LocalDate;

public class ReplenishTask {
    private final int productId;
    private final int count;
    private final int reservedCount;
    private final String reservationTicket;
    private final LocalDate deliveryDate;

    public ReplenishTask(int productId, int count, int reservedCount, String reservationTicket, LocalDate deliveryDate) {
        this.productId = productId;
        this.count = count;
        this.reservedCount = reservedCount;
        this.reservationTicket = reservationTicket;
        this.deliveryDate = deliveryDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public String getReservationTicket() {
        return reservationTicket;
    }

    public int getReservedCount() {
        return reservedCount;
    }

    public int getCount() {
        return count;
    }

    public int getProductId() {
        return productId;
    }

    public boolean completed() {
        return count == reservedCount;
    }

    public boolean shouldHaveArrived() {
        if (deliveryDate == null) {
            return true;
        }
        return deliveryDate.isBefore(LocalDate.now());
    }
}
