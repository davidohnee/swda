package ch.hslu.swda.entities;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class Product {
    private final UUID id;
    private final String name;
    private BigDecimal price;

    public Product(UUID id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof Product product)
                &&
                (product.id == this.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product [" +
                "id='" + this.id + '\'' +
                ", name='" + this.name + '\'' +
                ", price='" + this.price + '\'' +
                ']';
    }
}
