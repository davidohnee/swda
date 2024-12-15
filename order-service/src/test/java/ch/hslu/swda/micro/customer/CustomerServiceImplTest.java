package ch.hslu.swda.micro.customer;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.micro.customer.dto.CustomerValidationResponse;
import ch.hslu.swda.micro.customer.receiver.CustomerValidationResponseReceiver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private static final String EXCHANGE_NAME = "swda";
    @Mock
    private BusConnector busConnector;
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerService = new CustomerServiceImpl(busConnector, EXCHANGE_NAME);
    }

    @Test
    void validateCustomerShouldReturnTrueWhenCustomerIsValid() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerValidationResponse validResponse = new CustomerValidationResponse(customerId, true);
        String jsonResponse = new ObjectMapper().writeValueAsString(validResponse);

        doAnswer(invocation -> {
            String exchange = invocation.getArgument(0);
            String route = invocation.getArgument(1);
            String message = invocation.getArgument(2);
            CustomerValidationResponseReceiver receiver = invocation.getArgument(3);

            // Simulate the async behavior
            CompletableFuture.runAsync(() -> {
                // Simulate some delay
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Call onMessageReceived to simulate the response
                receiver.onMessageReceived(route, "replyTo", UUID.randomUUID().toString(), jsonResponse);
            });

            return null;
        }).when(busConnector).talkAsync(anyString(), anyString(), anyString(), any(CustomerValidationResponseReceiver.class));

        // When
        CompletableFuture<Boolean> result = customerService.validateCustomer(customerId);

        // Then
        assertThat(result).succeedsWithin(Duration.ofSeconds(1)).isEqualTo(true);
        verify(busConnector).talkAsync(eq(EXCHANGE_NAME), eq(MessageRoutes.CUSTOMER_VALIDATE), anyString(), any(CustomerValidationResponseReceiver.class));
    }

    @Test
    void validateCustomerShouldReturnFalseWhenCustomerIsNotValid() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerValidationResponse invalidResponse = new CustomerValidationResponse(customerId, false);
        String jsonResponse = new ObjectMapper().writeValueAsString(invalidResponse);

        doAnswer(invocation -> {
            String exchange = invocation.getArgument(0);
            String route = invocation.getArgument(1);
            String message = invocation.getArgument(2);
            CustomerValidationResponseReceiver receiver = invocation.getArgument(3);

            // Simulate the async behavior
            CompletableFuture.runAsync(() -> {
                // Simulate some delay
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Call onMessageReceived to simulate the response
                receiver.onMessageReceived(route, "replyTo", UUID.randomUUID().toString(), jsonResponse);
            });

            return null;
        }).when(busConnector).talkAsync(anyString(), anyString(), anyString(), any(CustomerValidationResponseReceiver.class));

        // When
        CompletableFuture<Boolean> result = customerService.validateCustomer(customerId);

        // Then
        assertThat(result).succeedsWithin(Duration.ofSeconds(1)).isEqualTo(false);
        verify(busConnector).talkAsync(eq(EXCHANGE_NAME), eq(MessageRoutes.CUSTOMER_VALIDATE), anyString(), any(CustomerValidationResponseReceiver.class));
    }

    @Test
    void validateCustomer_shouldCompleteExceptionallyWhenIOExceptionOccurs() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        doThrow(new IOException("Failed to send validation request")).when(busConnector).talkAsync(anyString(), anyString(), anyString(), any(MessageReceiver.class));

        // When
        CompletableFuture<Boolean> result = customerService.validateCustomer(customerId);

        // Then
        assertThat(result).isCompletedExceptionally();
        assertThatThrownBy(result::get)
                .hasCauseInstanceOf(CustomerValidateException.class)
                .hasMessageContaining("Failed to send validation request");
    }

    @Test
    void validateCustomer_shouldCompleteExceptionallyWhenInterruptedExceptionOccurs() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        doThrow(new InterruptedException("Test exception"))
                .when(busConnector)
                .talkAsync(anyString(), anyString(), anyString(), any(CustomerValidationResponseReceiver.class));

        // When
        CompletableFuture<Boolean> result = customerService.validateCustomer(customerId);

        // Then
        assertThat(result).isCompletedExceptionally();
        assertThatThrownBy(result::get)
                .hasCauseInstanceOf(CustomerValidateException.class)
                .hasMessageContaining("Failed to send validation request");
    }
}