package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class OrderItemTest {

    private Item item;

    @BeforeEach
    void setup() {
        Product product = new Product(1, "Product 1", new BigDecimal("100.0"));
        item = new Item(1, product);
    }

    @Test
    void testCreateOrderItem() {
        OrderItem orderItem = new OrderItem(item, 2, Warehouse.LOCAL);
        Item expectedItem = new Item(1, new Product(1, "Product 1", new BigDecimal("100.0")));
        Assertions.assertThat(orderItem.getItem()).isEqualTo(expectedItem);
        Assertions.assertThat(orderItem.getQuantity()).isEqualTo(2);
        Assertions.assertThat(orderItem.getWarehouse()).isEqualTo(Warehouse.LOCAL);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(OrderItem.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}