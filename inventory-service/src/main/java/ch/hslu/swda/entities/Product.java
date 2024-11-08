package ch.hslu.swda.entities;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

public final class Product {
    private final int id;
    private final String name;
    private BigDecimal price;

    public Product(int id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static int randomId() {
        final int min = 100_000;
        final int max = Integer.MAX_VALUE;
        Random random = new Random();
        return random.nextInt(max - min) + min;
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
