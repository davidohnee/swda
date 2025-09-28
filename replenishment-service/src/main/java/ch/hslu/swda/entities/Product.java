package ch.hslu.swda.entities;

import java.math.BigDecimal;
import java.util.Objects;

public final class Product {
    private final int id;
    private final String name;
    private BigDecimal price;

    public Product(int id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Product() {
        this(0, "", BigDecimal.ZERO);
    }

    public int getId() {
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
                && product.getId() == this.id;
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
