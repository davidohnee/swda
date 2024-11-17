package ch.hslu.swda.common.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

@JsonPropertyOrder({
        "id",
        "name",
        "price"
})
@JsonTypeName("Product")
@Introspected
public class Product {
    private int id;
    private String name;
    private BigDecimal price;

    @JsonCreator
    public Product(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("price") BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Product() {
        this.id = randomId();
        this.name = "";
        this.price = BigDecimal.ZERO;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof Product product)
                && (product.id == this.id);
    }

    @Override
    public final int hashCode() {
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

    public static int randomId() {
        final int min = 100_000;
        final int max = Integer.MAX_VALUE;
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
