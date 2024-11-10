package ch.hslu.swda.entities;

import ch.hslu.swda.dto.inventory.InventoryUpdateRequest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class InMemoryInventoryUpdateRequestTest {
    @Test
    void testCreateOrderItemTest() {
        int productId = 111222;
        InventoryUpdateRequest request = new InventoryUpdateRequest(productId, 10);
        Assertions.assertThat(request.getQuantity()).isEqualTo(10);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(InventoryUpdateRequest.class)
                .verify();
    }
}
