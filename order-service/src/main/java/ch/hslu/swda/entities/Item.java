package ch.hslu.swda.entities;

import java.util.Objects;

public final class Item {
    private final long id;
    private final Product product;

    public Item(long id, Product product) {
        this.id = id;
        this.product = product;
    }

    public long getId() {
        return this.id;
    }

    public Product getProduct() {
        return new Product(this.product.getId(), this.product.getName(), this.product.getPrice());
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof Item item)
                &&
                (item.id == this.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Item [" +
                "id='" + this.id + '\'' +
                ", product='" + this.product + '\'' +
                ']';
    }
}
