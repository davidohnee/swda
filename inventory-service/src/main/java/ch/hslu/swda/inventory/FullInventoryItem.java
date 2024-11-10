package ch.hslu.swda.inventory;

import ch.hslu.swda.dto.inventory.InventoryItem;
import ch.hslu.swda.entities.Product;

public class FullInventoryItem extends InventoryItem {
    private int preorderedQuantity;
    private int replenishingQuantity;

    public FullInventoryItem(Product product, int quantity, int preorderedQuantity) {
        super(product, quantity);
        this.preorderedQuantity = preorderedQuantity;
    }

    public FullInventoryItem(Product product, int quantity) {
        this(product, quantity, 0);
    }

    public void preorder(int i) {
        this.preorderedQuantity += i;
    }

    public void handleReplenishment(int i) {
        if (i > preorderedQuantity) {
            this.quantity += i - preorderedQuantity;
            preorderedQuantity = 0;
        } else {
            preorderedQuantity -= i;
        }
    }
}
