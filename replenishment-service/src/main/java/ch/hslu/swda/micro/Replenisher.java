package ch.hslu.swda.micro;

import ch.hslu.swda.entities.ReplenishmentStatus;
import ch.hslu.swda.models.ReplenishTask;
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

    public ReplenishmentStatus replenish(int productId, int count) {
        int inStock = this.stock.getItemCount(productId);
        if (inStock < count) {
            // not enough in stock; reserve all and wait for replenishment
            String ticket = this.stock.reserveItem(productId, inStock);
            LocalDate deliveryDate = this.stock.getItemDeliveryDate(productId);

            ReplenishTask task = new ReplenishTask(productId, count, inStock, ticket, deliveryDate);
            tasks.add(task);
            return ReplenishmentStatus.CONFIRMED;
        } else {
            // enough in stock; reserve requested amount
            this.stock.orderItem(productId, count);
            return ReplenishmentStatus.DONE;
        }
    }
}
