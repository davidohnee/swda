package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class OrderItemTest {
    @Test
    void testCreateInventoryItemUpdateRequest() {
        UUID productId = UUID.randomUUID();
        OrderItem request = new OrderItem(productId, 10);
        Assertions.assertThat(request.getQuantity()).isEqualTo(10);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(OrderItem.class)
                .verify();
    }
}
