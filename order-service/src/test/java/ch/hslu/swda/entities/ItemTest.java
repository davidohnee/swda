package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ItemTest {

    private Product product;

    @BeforeEach
    void setup() {
        product = new Product(1, "Product 1", new BigDecimal("10.0"));
    }

    @Test
    void testCreateItem() {
        Item item = new Item(1, product);
        Product expectedProduct = new Product(1, "Product 1", new BigDecimal("10.0"));
        Assertions.assertThat(item.getId()).isEqualTo(1);
        Assertions.assertThat(item.getProduct()).isEqualTo(expectedProduct);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(Item.class)
                .withOnlyTheseFields("id")
                .verify();
    }

}