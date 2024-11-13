package ch.hslu.swda.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class InventoryTakeItemsResponseTest {

    @Test
    void testInventoryTakeItemsResponse() {
        UUID trackingId = UUID.randomUUID();
        int productId = 1;
        OrderItemStatus status = OrderItemStatus.NOT_FOUND;
        InventoryTakeItemsResponse inventoryTakeItemsResponse = new InventoryTakeItemsResponse(trackingId, productId, status);
        Assertions.assertThat(inventoryTakeItemsResponse.getTrackingId()).isEqualTo(trackingId);
        Assertions.assertThat(inventoryTakeItemsResponse.getProductId()).isEqualTo(productId);
        Assertions.assertThat(inventoryTakeItemsResponse.getStatus()).isEqualTo(status);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(InventoryTakeItemsResponse.class).verify();
    }

}