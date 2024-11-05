package ch.hslu.swda.entities;

public class ReplenishmentOrder {
    private final int productId;
    private final int count;

    public ReplenishmentOrder(int productId, int count) {
        this.productId = productId;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getProductId() {
        return productId;
    }
}
