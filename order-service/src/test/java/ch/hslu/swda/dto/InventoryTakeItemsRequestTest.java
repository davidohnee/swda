package ch.hslu.swda.dto;

import ch.hslu.swda.model.OrderItemCreate;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class InventoryTakeItemsRequestTest {

    @Test
    void testInventoryTakeItemsRequest() {
        List<OrderItemCreate> orderItems = List.of(new OrderItemCreate(300_000, 10));
        InventoryTakeItemsRequest request = new InventoryTakeItemsRequest(orderItems);
        Assertions.assertThat(request.getItems()).isEqualTo(orderItems);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(InventoryTakeItemsRequest.class).verify();
    }

}