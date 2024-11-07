package ch.hslu.swda.micro;

import ch.hslu.swda.entities.ReplenishmentOrderResponse;
import ch.hslu.swda.entities.ReplenishmentStatus;
import ch.hslu.swda.models.ReplenishTask;
import ch.hslu.swda.models.ReplenishTaskReservation;
import ch.hslu.swda.stock.api.Stock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Replenisher {
    private final Stock stock;
    private final List<ReplenishTask> tasks = new ArrayList<>();

    public Replenisher(final Stock stock) {
        this.stock = stock;
    }

    public ReplenishmentOrderResponse replenish(int productId, int count) {
        int inStock = this.stock.getItemCount(productId);
        ReplenishmentStatus status = ReplenishmentStatus.PENDING;

        if (inStock < count) {
            // not enough in stock; reserve all and wait for replenishment
            String ticket = this.stock.reserveItem(productId, inStock);
            LocalDate deliveryDate = this.stock.getItemDeliveryDate(productId);

            ReplenishTaskReservation reservation = new ReplenishTaskReservation(inStock, ticket, deliveryDate);
            ReplenishTask task = new ReplenishTask(productId, count, reservation);
            tasks.add(task);
            status = ReplenishmentStatus.CONFIRMED;
        } else {
            // enough in stock; order requested amount
            this.stock.orderItem(productId, count);
            status = ReplenishmentStatus.DONE;
        }

        return new ReplenishmentOrderResponse(productId, status);
    }
}
