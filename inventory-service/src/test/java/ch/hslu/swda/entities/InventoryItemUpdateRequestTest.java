package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class InventoryItemUpdateRequestTest {
    @Test
    void testCreateInventoryItemUpdateRequest() {
        UUID productId = UUID.randomUUID();
        InventoryItemUpdateRequest request = new InventoryItemUpdateRequest(productId, 10);
        Assertions.assertThat(request.getCount()).isEqualTo(10);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(InventoryItemUpdateRequest.class)
                .verify();
    }
}
