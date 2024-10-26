package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ProductTest {

    @Test
    void testCreateProduct() {
        Product product = new Product(1, "Product 1", new BigDecimal("100.0"));
        Assertions.assertThat(product.getId()).isEqualTo(1);
        Assertions.assertThat(product.getName()).isEqualTo("Product 1");
        Assertions.assertThat(product.getPrice()).isEqualTo(new BigDecimal("100.0"));
    }

    @Test
    void testSetPrice() {
        Product product = new Product(1, "Product 1", new BigDecimal("100.0"));
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