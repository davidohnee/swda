package ch.hslu.swda.micro.inventory.dto;


import ch.hslu.swda.common.entities.OrderItemCreate;

import java.util.List;
import java.util.Objects;

public final class InventoryUpdateItemsRequest {
    private final List<OrderItemCreate> items;

    public InventoryUpdateItemsRequest(List<OrderItemCreate> items) {
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
        return (object instanceof InventoryUpdateItemsRequest that)
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
