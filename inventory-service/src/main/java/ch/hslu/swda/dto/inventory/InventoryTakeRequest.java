package ch.hslu.swda.dto.inventory;

import java.util.List;

public class InventoryTakeRequest {
    List<InventoryUpdateRequest> items;

    public InventoryTakeRequest(List<InventoryUpdateRequest> items) {
        this.items = items;
    }

    public InventoryTakeRequest() {
        this(List.of());
    }

    public List<InventoryUpdateRequest> getItems() {
        return items;
    }
}
