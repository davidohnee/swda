package ch.hslu.swda.entities;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderBuilderTest {

    @Test
    void testBuilder() {
        Order order = new OrderBuilder()
                .setId(1)
                .setTimestamp(new java.util.Date())
                .setStatus(OrderStatus.NEW)
                .setItems(java.util.Collections.emptyList())
                .build();
        Assertions.assertThat(order.getId()).isEqualTo(1);
        Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
        Assertions.assertThat(order.getItems()).isEmpty();
    }

}