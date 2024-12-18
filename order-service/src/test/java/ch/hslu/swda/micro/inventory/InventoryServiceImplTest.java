package ch.hslu.swda.micro.inventory;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.common.entities.OrderItemCreate;
import ch.hslu.swda.common.entities.OrderItemStatus;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    private static final String EXCHANGE_NAME = "swda";
    private static final int PRODUCT_ID = 100_005;
    private static final int QUANTITY = 10;
    @Mock
    private BusConnector busConnector;
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventoryService = new InventoryServiceImpl(busConnector, EXCHANGE_NAME);
    }

    @Test
    void takeItemsShouldReturnOrderInfoArrayWhenSuccessful() throws Exception {
        // Given
        List<OrderItemCreate> orderItems = List.of(new OrderItemCreate(PRODUCT_ID, QUANTITY));
        OrderInfo expectedOrderInfo = new OrderInfo(UUID.randomUUID(), PRODUCT_ID, OrderItemStatus.NOT_FOUND, 0, null, BigDecimal.ZERO);
        OrderInfo[] expectedResponse = { expectedOrderInfo };
        String jsonResponse = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(expectedResponse);

        doAnswer(invocation -> {
            String exchange = invocation.getArgument(0);
            String route = invocation.getArgument(1);
            String message = invocation.getArgument(2);
            InventoryTakeItemsResponseReceiver receiver = invocation.getArgument(3);

            // Simulate the async behavior
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(100); // Simulate some delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                receiver.onMessageReceived(route, "replyTo", UUID.randomUUID().toString(), jsonResponse);
            });

            return null;
        }).when(busConnector).talkAsync(anyString(), anyString(), anyString(), any(InventoryTakeItemsResponseReceiver.class));

        // When
        CompletableFuture<OrderInfo[]> result = inventoryService.takeItems(orderItems);

        // Then
        assertThat(result).succeedsWithin(Duration.ofSeconds(1)).satisfies(orderInfoArray -> {
            assertThat(orderInfoArray).hasSize(1);
            assertThat(orderInfoArray[0]).isEqualTo(expectedOrderInfo);
        });

        verify(busConnector).talkAsync(eq(EXCHANGE_NAME), eq(MessageRoutes.INVENTORY_TAKE), anyString(), any(InventoryTakeItemsResponseReceiver.class));
    }


    @Test
    void takeItemsShouldFailWhenIOExceptionOccurs() throws Exception {
        // Given
        List<OrderItemCreate> orderItems = List.of(new OrderItemCreate(PRODUCT_ID, QUANTITY));
        doThrow(new IOException("Failed to send request"))
                .when(busConnector)
                .talkAsync(anyString(), anyString(), anyString(), any(InventoryTakeItemsResponseReceiver.class));

        // When
        CompletableFuture<OrderInfo[]> result = inventoryService.takeItems(orderItems);

        // Then
        assertThat(result).isCompletedExceptionally();
        assertThatThrownBy(result::get)
                .hasCauseInstanceOf(InventoryTakeItemsException.class);
    }

    @Test
    void takeItemShouldSendMessageSuccessfully() throws Exception {
        // Given
        OrderItemCreate orderItem = new OrderItemCreate(PRODUCT_ID, QUANTITY);
        MessageReceiver receiver = mock(MessageReceiver.class);

        // When
        inventoryService.takeItem(orderItem, receiver);

        // Then
        verify(busConnector).talkAsync(
                eq(EXCHANGE_NAME),
                eq(MessageRoutes.INVENTORY_TAKE),
                contains("\"quantity\":10"),
                eq(receiver)
        );
    }

    @Test
    void returnItemsShouldSendMessageSuccessfully() throws Exception {
        // Given
        List<OrderItemCreate> orderItems = List.of(new OrderItemCreate(PRODUCT_ID, QUANTITY));
        MessageReceiver receiver = mock(MessageReceiver.class);

        // When
        inventoryService.returnItems(orderItems, receiver);

        // Then
        verify(busConnector).talkAsync(
                eq(EXCHANGE_NAME),
                eq(MessageRoutes.INVENTORY_ADD),
                eq("{\"items\":[{\"productId\":100005,\"quantity\":10}]}"),
                eq(receiver)
        );
    }

    @Test
    void cancelReplenishmentShouldSendMessageSuccessfully() throws Exception {
        // Given
        UUID trackingId = UUID.randomUUID();
        MessageReceiver receiver = mock(MessageReceiver.class);

        // When
        inventoryService.cancelReplenishment(trackingId, receiver);

        // Then
        verify(busConnector).talkAsync(
                eq(EXCHANGE_NAME),
                eq(MessageRoutes.INVENTORY_CANCEL),
                contains(trackingId.toString()),
                eq(receiver)
        );
    }
}
