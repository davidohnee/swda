package ch.hslu.swda.micro;

import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemStatus;
import ch.hslu.swda.dto.replenishment.ReplenishmentOrderResponse;
import ch.hslu.swda.entities.ReplenishmentStatus;
import ch.hslu.swda.micro.senders.InventoryClient;
import ch.hslu.swda.micro.senders.OnItemReplenished;
import ch.hslu.swda.models.ReplenishTask;
import ch.hslu.swda.models.ReplenishTaskReservation;
import ch.hslu.swda.stock.api.Stock;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Replenisher {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Replenisher.class);
    private final Stock stock;
    private final List<ReplenishTask> tasks = new ArrayList<>();
    private final InventoryClient inventoryItemGetter;
    private final OnItemReplenished onItemReplenished;

    public Replenisher(final Stock stock, InventoryClient inventoryItemGetter, OnItemReplenished onItemReplenished) {
        this.stock = stock;
        this.inventoryItemGetter = inventoryItemGetter;
        this.onItemReplenished = onItemReplenished;
    }

    public OrderInfo replenish(int productId, int count) throws IOException, InterruptedException {
        int inStock = this.stock.getItemCount(productId);
        OrderItemStatus status;
        int immediatelyAvailableQuantity;
        ReplenishTaskReservation reservation = null;
        LocalDate deliveryDate = null;

        LOG.debug("Replenishing product {} with count {} and in stock {}", productId, count, inStock);

        if (inStock < count) {
            // not enough in stock; reserve all and wait for replenishment
            String ticket = this.stock.reserveItem(productId, inStock);

            // check again next week??
            var checkAgain = LocalDate.now().plusWeeks(1);

            LOG.info("Not enough in stock for product {} ({} < {}), will check again on {}",
                    productId, inStock, count, checkAgain);

            reservation = new ReplenishTaskReservation(inStock, ticket, checkAgain);

            immediatelyAvailableQuantity = 0;
            status = OrderItemStatus.PENDING;
        } else {
            // enough in stock; provide requested amount
            this.stock.orderItem(productId, count);

            deliveryDate = this.stock.getItemDeliveryDate(productId);

            LOG.info("Enough in stock for product {} ({} >= {}), will be delivered on {}",
                    productId, inStock, count, deliveryDate);

            immediatelyAvailableQuantity = count;
            status = OrderItemStatus.CONFIRMED;
        }

        var task = new ReplenishTask(productId, count, reservation, deliveryDate);
        tasks.add(task);

        this.inventoryItemGetter.getInventoryItem(productId, (inventoryItem) -> {
            LOG.info("Received inventory item: {}", inventoryItem);
            task.setProduct(inventoryItem.getProduct());
        });

        return new OrderInfo(
                productId,
                status,
                immediatelyAvailableQuantity,
                task.getDeliveryDate()
        );
    }

    public void onTimer() {
        LOG.debug("Checking replenishment tasks");
        List<ReplenishTask> doneTasks = new ArrayList<>();
        for (ReplenishTask task : tasks) {
            if (!task.getReservation().shouldHaveArrived()) {
                continue;
            }

            var nowInStock = this.stock.getItemCount(task.getProductId());
            var missing = task.getCount() - task.getReservation().getReservedCount();
            if (nowInStock < missing) {
                task.getReservation().setCheckAgain(LocalDate.now().plusWeeks(1));
                LOG.info("Still not enough items of {}, check again on {}",
                        task.getProductId(), task.getReservation().getCheckAgain());
                continue;
            }

            LOG.info("Item {} restocked", task.getProductId());
            this.stock.freeReservation(task.getReservation().getReservationTicket());
            this.stock.orderItem(task.getProductId(), task.getCount());
            doneTasks.add(task);
        }

        for (ReplenishTask task : doneTasks) {
            tasks.remove(task);
            this.onItemReplenished.onItemReplenished(new ReplenishmentOrderResponse(
                    task.getTrackingId(),
                    task.getProductId(),
                    ReplenishmentStatus.CONFIRMED,
                    task.getCount(),
                    task.getDeliveryDate()
            ));
        }
    }

    public List<ReplenishTask> getTasks() {
        return tasks;
    }
}
