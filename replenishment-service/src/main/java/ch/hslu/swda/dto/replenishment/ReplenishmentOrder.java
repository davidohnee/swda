package ch.hslu.swda.dto.replenishment;

public class ReplenishmentOrder {
    private final int productId;
    private final int quantity;

    public ReplenishmentOrder(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public ReplenishmentOrder() {
        this(0, 0);
    }

    public int getQuantity() {
        return quantity;
    }

    public int getProductId() {
        return productId;
    }
}
