package ch.hslu.swda.common.entities;

import java.time.LocalDate;
import java.util.Objects;

public class ReplenishmentReservation {
    private final int reservedCount;
    private final String reservationTicket;
    private LocalDate checkAgain;

    public ReplenishmentReservation(int reservedCount, String reservationTicket, LocalDate checkAgain) {
        this.reservedCount = reservedCount;
        this.reservationTicket = reservationTicket;
        this.checkAgain = checkAgain;
    }

    public LocalDate getCheckAgain() {
        return checkAgain;
    }

    public void setCheckAgain(LocalDate checkAgain) {
        this.checkAgain = checkAgain;
    }

    public String getReservationTicket() {
        return reservationTicket;
    }

    public int getReservedCount() {
        return reservedCount;
    }

    public boolean shouldHaveArrived() {
        if (checkAgain == null) {
            return true;
        }
        return checkAgain.isBefore(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplenishmentReservation that)) return false;
        return reservedCount == that.reservedCount && Objects.equals(reservationTicket, that.reservationTicket) && Objects.equals(checkAgain, that.checkAgain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservedCount, reservationTicket, checkAgain);
    }
}
