package ch.hslu.swda.entities;

import java.util.List;

public class InventoryTakeItemsRequest {
    List<OrderItem> items;

    public InventoryTakeItemsRequest(List<OrderItem> items) {
        this.items = items;
    }

    public InventoryTakeItemsRequest() {
        this(List.of());
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
