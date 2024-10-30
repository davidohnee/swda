package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class GetOrderReceiverTest {

    private GetOrderReceiver getOrderReceiver;
    private BusConnector busConnector;
    private OrdersMemory ordersMemory;
    private ObjectMapper objectMapper;
    private Order order;

    @BeforeEach
    public void setUp() {
        busConnector = mock(BusConnector.class);
        ordersMemory = mock(OrdersMemory.class);
        order = mock(Order.class);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        getOrderReceiver = new GetOrderReceiver("testExchange", busConnector, ordersMemory);
    }

    @Test
    public void testOnMessageReceived_OrderGetEntity() throws IOException {
        UUID orderId = UUID.randomUUID();
        when(ordersMemory.getOrderById(orderId)).thenReturn(order);

        String message = "\"" + orderId + "\"";
        getOrderReceiver.onMessageReceived(MessageRoutes.ORDER_GET_ENTITY, "replyTo", "corrId", message);

        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq("testExchange"), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        String expectedResponse = objectMapper.writeValueAsString(order);
        Assertions.assertThat(responseCaptor.getValue()).isEqualTo(expectedResponse);
    }

    @Test
    public void testOnMessageReceived_OrderGetEntity_OrderNotFound() throws IOException {
        UUID orderId = UUID.randomUUID();
        when(ordersMemory.getOrderById(orderId)).thenReturn(null);

        String message = "\"" + orderId + "\"";
        getOrderReceiver.onMessageReceived(MessageRoutes.ORDER_GET_ENTITY, "replyTo", "corrId", message);

        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq("testExchange"), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        Assertions.assertThat(responseCaptor.getValue()).isEqualTo("Order not found");
    }

    @Test
    public void testOnMessageReceivedOrderGetEntitySet() throws IOException {
        List<Order> orders = List.of(order, order, order);
        when(ordersMemory.getAllOrders()).thenReturn(orders);

        getOrderReceiver.onMessageReceived(MessageRoutes.ORDER_GET_ENTITYSET, "replyTo", "corrId", "");

        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq("testExchange"), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        String expectedResponse = objectMapper.writeValueAsString(orders);
        Assertions.assertThat(responseCaptor.getValue()).isEqualTo(expectedResponse);
    }

    @Test
    public void testOnMessageReceivedUnknownRoute() throws IOException {
        getOrderReceiver.onMessageReceived("unknownRoute", "replyTo", "corrId", "");

        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq("testExchange"), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        Assertions.assertThat(responseCaptor.getValue()).isEqualTo("Unknown route");
    }

    @Test
    public void testOnMessageReceivedInvalidUUIDFormat() throws IOException {
        String invalidUUID = "invalid-uuid";
        getOrderReceiver.onMessageReceived(MessageRoutes.ORDER_GET_ENTITY, "replyTo", "corrId", invalidUUID);

        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq("testExchange"), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        Assertions.assertThat(responseCaptor.getValue()).isEqualTo("Invalid UUID format");
    }
}