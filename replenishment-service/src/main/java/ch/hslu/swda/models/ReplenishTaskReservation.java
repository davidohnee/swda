package ch.hslu.swda.models;

import java.time.LocalDate;
import java.util.Objects;

public class ReplenishTaskReservation {
    private final int reservedCount;
    private final String reservationTicket;
    private final LocalDate deliveryDate;

    public ReplenishTaskReservation(int reservedCount, String reservationTicket, LocalDate deliveryDate) {
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

    public boolean shouldHaveArrived() {
        if (deliveryDate == null) {
            return true;
        }
        return deliveryDate.isBefore(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplenishTaskReservation that)) return false;
        return reservedCount == that.reservedCount && Objects.equals(reservationTicket, that.reservationTicket) && Objects.equals(deliveryDate, that.deliveryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservedCount, reservationTicket, deliveryDate);
    }
}
