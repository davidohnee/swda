package ch.hslu.swda.entities;

import java.util.Objects;

public final class OrderItem {
    private final Item item;
    private final int quantity;
    private Warehouse warehouse;

    public OrderItem(Item item, int quantity, Warehouse warehouse) {
        this.item = item;
        this.quantity = quantity;
        this.warehouse = warehouse;
    }

    public Item getItem() {
        return new Item(this.item.getId(), this.item.getProduct());
    }

    public int getQuantity() {
        return this.quantity;
    }

    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof OrderItem orderItem)
                && orderItem.quantity == this.quantity
                && Objects.equals(orderItem.item, this.item)
                && warehouse == orderItem.warehouse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, quantity, warehouse);
    }

    @Override
    public String toString() {
        return "OrderItem [" +
                "item='" + this.item + '\'' +
                ", quantity='" + this.quantity + '\'' +
                ", warehouse='" + this.warehouse + '\'' +
                ']';
    }
}
