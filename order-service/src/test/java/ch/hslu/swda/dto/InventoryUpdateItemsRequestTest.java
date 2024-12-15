package ch.hslu.swda.dto;

import ch.hslu.swda.common.entities.OrderItemCreate;
import ch.hslu.swda.micro.inventory.dto.InventoryUpdateItemsRequest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class InventoryUpdateItemsRequestTest {

    @Test
    void testInventoryTakeItemsRequest() {
        List<OrderItemCreate> orderItems = List.of(new OrderItemCreate(300_000, 10));
        InventoryUpdateItemsRequest request = new InventoryUpdateItemsRequest(orderItems);
        Assertions.assertThat(request.getItems()).isEqualTo(orderItems);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(InventoryUpdateItemsRequest.class).verify();
    }

}