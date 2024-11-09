package ch.hslu.swda.dto;

import ch.hslu.swda.model.OrderItemCreate;

import java.util.List;
import java.util.Objects;

public final class InventoryTakeItemsRequest {
    private final List<OrderItemCreate> items;

    public InventoryTakeItemsRequest(List<OrderItemCreate> items) {
        this.items = items;
    }

    public List<OrderItemCreate> getItems() {
        return this.items;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof InventoryTakeItemsRequest that)
                && Objects.equals(that.items, this.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return "TakeItemRequest [" +
                "orderItems=" + items +
                ']';
    }
}
