package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

class OrderTest {

    @Test
    void testCreateOrder() {
        Order order = new OrderBuilder()
                .setId(1)
                .setTimestamp(new Date())
                .setStatus(OrderStatus.NEW)
                .setItems(Collections.emptyList())
                .build();
        Assertions.assertThat(order.getId()).isEqualTo(1);
        Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
        Assertions.assertThat(order.getItems()).isEmpty();
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(Order.class)
                .withOnlyTheseFields("id")
                .verify();
    }

}