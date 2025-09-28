package ch.hslu.swda.micro;

import ch.hslu.swda.common.database.ReplenishmentDAO;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemStatus;
import ch.hslu.swda.common.entities.ReplenishmentReservation;
import ch.hslu.swda.dto.replenishment.ReplenishmentOrderResponse;
import ch.hslu.swda.entities.ReplenishmentStatus;
import ch.hslu.swda.micro.senders.InventoryClient;
import ch.hslu.swda.micro.senders.OnItemReplenished;
import ch.hslu.swda.models.ReplenishTask;
import ch.hslu.swda.stock.api.Stock;
import com.mongodb.client.MongoDatabase;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Replenisher {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Replenisher.class);
    private final Stock stock;
    private final List<ReplenishTask> tasks = new ArrayList<>();
    private final InventoryClient inventoryItemGetter;
    private final OnItemReplenished onItemReplenished;
    private final ReplenishmentDAO database;

    public Replenisher(
            final Stock stock,
            InventoryClient inventoryItemGetter,
            OnItemReplenished onItemReplenished,
            MongoDatabase database
    ) {
        this.stock = stock;
        this.inventoryItemGetter = inventoryItemGetter;
        this.onItemReplenished = onItemReplenished;
        this.database = new ReplenishmentDAO(database);

        this.loadFromDatabase();
    }

    private void loadFromDatabase() {
        LOG.info("Loading replenishment tasks from database");
        var orders = this.database.getAll();
        for (var order : orders) {
            var task = ReplenishTask.fromSimpleReplenishmentOrder(order);
            this.tasks.add(task);
            try {
                this.inventoryItemGetter.getInventoryItem(order.getProductId(), (inventoryItem) -> {
                    LOG.info("<init> Received inventory item: {}", inventoryItem);
                    task.setProduct(inventoryItem.getProduct());
                });
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.info("Loaded {} replenishment tasks from database", tasks.size());
    }

    public OrderInfo replenish(int productId, int count) throws IOException, InterruptedException {
        int inStock = this.stock.getItemCount(productId);
        OrderItemStatus status;
        int immediatelyAvailableQuantity;
        ReplenishmentReservation reservation = null;
        LocalDate deliveryDate = null;

        LOG.debug("Replenishing product {} with count {} and in stock {}", productId, count, inStock);

        if (inStock < count) {
            // not enough in stock; reserve all and wait for replenishment
            String ticket = this.stock.reserveItem(productId, inStock);

            // check again next week??
            var checkAgain = LocalDate.now().plusWeeks(1);

            LOG.info("Not enough in stock for product {} ({} < {}), will check again on {}",
                    productId, inStock, count, checkAgain);

            reservation = new ReplenishmentReservation(inStock, ticket, checkAgain);

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
        this.database.create(task.toSimpleReplenishmentOrder());

        this.inventoryItemGetter.getInventoryItem(productId, (inventoryItem) -> {
            LOG.info("Received inventory item: {}", inventoryItem);
            task.setProduct(inventoryItem.getProduct());
        });

        return new OrderInfo(
                productId,
                status,
                immediatelyAvailableQuantity,
                task.getDeliveryDate(),
                null
        );
    }

    private boolean checkTaskAgain(ReplenishTask task) {
        if (task.getReservation() == null) {
            return false;
        }

        if (task.getReservation().getCheckAgain().isBefore(LocalDate.now())) {
            LOG.info("Checking replenishment task for product {} again", task.getProductId());
            return true;
        }

        var nowInStock = this.stock.getItemCount(task.getProductId());
        var missing = task.getCount() - task.getReservation().getReservedCount();
        if (nowInStock < missing) {
            task.getReservation().setCheckAgain(LocalDate.now().plusWeeks(1));
            LOG.info("Still not enough items of {}, check again on {}",
                    task.getProductId(), task.getReservation().getCheckAgain());
            return true;
        }

        LOG.info("Item {} restocked", task.getProductId());
        this.stock.freeReservation(task.getReservation().getReservationTicket());
        task.setReservation(null);
        this.stock.orderItem(task.getProductId(), task.getCount());
        task.setDeliveryDate(this.stock.getItemDeliveryDate(task.getProductId()));
        this.database.update(task.toSimpleReplenishmentOrder());
        return true;
    }

    public void onTimer() {
        LOG.debug("Checking replenishment tasks");
        List<ReplenishTask> doneTasks = new ArrayList<>();
        for (ReplenishTask task : tasks) {
            LOG.debug("Checking replenishment task {}", task.toString());

            if (this.checkTaskAgain(task)) {
                continue;
            }

            if (task.completed()) {
                doneTasks.add(task);
            }
        }

        for (ReplenishTask task : doneTasks) {
            tasks.remove(task);
            this.database.delete(task.getId());
            this.onItemReplenished.onItemReplenished(
                task.toReplenishmentOrderResponse(ReplenishmentStatus.DONE)
            );
        }
    }

    public List<ReplenishTask> getTasks() {
        return tasks;
    }

    public ReplenishTask getTask(UUID trackingId) {
        for (ReplenishTask task : tasks) {
            if (task.getTrackingId().equals(trackingId)) {
                return task;
            }
        }
        return null;
    }

    public ReplenishmentOrderResponse cancelTask(UUID trackingId) {
        ReplenishTask task = this.getTask(trackingId);
        if (task == null) {
            return null;
        }

        if (task.getReservation() != null) {
            this.stock.freeReservation(task.getReservation().getReservationTicket());
        }
        this.tasks.remove(task);
        this.database.delete(task.getId());

        var response = task.toReplenishmentOrderResponse(ReplenishmentStatus.CANCELLED);

        this.onItemReplenished.onItemReplenished(
            response
        );

        return response;
    }
}
