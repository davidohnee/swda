package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

class InventoryItemTest {

    Product randomProduct() {
        Random random = new Random();
        int productId = random.nextInt() + 100000;
        return new Product(productId, "Product 1", new BigDecimal("100.0"));
    }

    @Test
    void testCreateInventoryItem() {
        Product product = this.randomProduct();
        InventoryItem item = new InventoryItem(product, 10);

        Assertions.assertThat(item.getProduct()).isEqualTo(product);
        Assertions.assertThat(item.getQuantity()).isEqualTo(10);
    }

    @Test
    void testSetQuantity() {
        Product product = this.randomProduct();
        InventoryItem item = new InventoryItem(product, 10);

        item.setQuantity(20);
        Assertions.assertThat(item.getQuantity()).isEqualTo(20);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(InventoryItem.class)
                .withIgnoredFields("quantity")
                .verify();
    }
}
