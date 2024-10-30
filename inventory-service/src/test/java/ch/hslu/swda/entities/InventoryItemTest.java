package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class InventoryItemTest {

    Product randomProduct() {
        return new Product(UUID.randomUUID(), "Product 1", new BigDecimal("100.0"));
    }

    @Test
    void testCreateInventoryItem() {
        Product product = this.randomProduct();
        InventoryItem item = new InventoryItem(product, 10);

        Assertions.assertThat(item.getProduct()).isEqualTo(product);
        Assertions.assertThat(item.getCount()).isEqualTo(10);
    }

    @Test
    void testSetCount() {
        Product product = this.randomProduct();
        InventoryItem item = new InventoryItem(product, 10);

        item.setCount(20);
        Assertions.assertThat(item.getCount()).isEqualTo(20);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(InventoryItem.class)
                .withIgnoredFields("count")
                .verify();
    }
}
