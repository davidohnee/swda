package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class ProductTest {

    @Test
    void testCreateProduct() {
        int id = Product.randomId();
        Product product = new Product(id, "Product 1", new BigDecimal("100.0"));
        Assertions.assertThat(product.getId()).isEqualTo(id);
        Assertions.assertThat(product.getName()).isEqualTo("Product 1");
        Assertions.assertThat(product.getPrice()).isEqualTo(new BigDecimal("100.0"));
    }

    @Test
    void testSetPrice() {
        Product product = new Product(Product.randomId(), "Product 1", new BigDecimal("100.0"));
        product.setPrice(new BigDecimal("200.0"));
        Assertions.assertThat(product.getPrice()).isEqualTo(new BigDecimal("200.0"));
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(Product.class)
                .withOnlyTheseFields("id")
                .verify();
    }

}