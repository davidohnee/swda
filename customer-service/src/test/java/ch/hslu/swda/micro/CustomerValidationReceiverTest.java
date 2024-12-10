package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.database.CustomerDAO;
import ch.hslu.swda.dto.CustomerValidationRequest;
import ch.hslu.swda.dto.CustomerValidationResponse;
import ch.hslu.swda.common.entities.Customer;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.micro.receivers.CustomerValidationReceiver;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

public class CustomerValidationReceiverTest {

    private static final String EXCHANGE_NAME = "dummy-exchange";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BusConnector busConnector;
    private Customer customer;
    private CustomerDAO customerDAO;
    private CustomerValidationReceiver customerValidationReceiver;

    @BeforeEach
    public void setUp() {
        busConnector = mock(BusConnector.class);
        customer = mock(Customer.class);
        customerDAO = mock(CustomerDAO.class);
        customerValidationReceiver = new CustomerValidationReceiver(EXCHANGE_NAME, busConnector, customerDAO);
    }

    @Test
    public void testOnMessageReceived_ValidationCustomer() throws IOException {
        //Arrange
        CustomerValidationRequest customerValidationRequest = new CustomerValidationRequest();
        when(customerDAO.read(customerValidationRequest.getCustomerId())).thenReturn(customer);

        //Act
        customerValidationReceiver.onMessageReceived(MessageRoutes.CUSTOMER_VALIDATE, "replyTo", "corrId", MAPPER.writeValueAsString(customerValidationRequest));
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq(EXCHANGE_NAME), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        //Assert
        String expectedResponse = MAPPER.writeValueAsString(new CustomerValidationResponse(customerValidationRequest.getCustomerId(), true));
        Assertions.assertThat(responseCaptor.getValue()).isEqualTo(expectedResponse);
    }

    @Test
    public void testOnMessageReceived_ValidationCustomer_NotFound() throws IOException {
        //Arrange
        CustomerValidationRequest customerValidationRequest = new CustomerValidationRequest();
        when(customerDAO.read(customerValidationRequest.getCustomerId())).thenReturn(null);

        //Act
        customerValidationReceiver.onMessageReceived(MessageRoutes.CUSTOMER_VALIDATE, "replyTo", "corrId", MAPPER.writeValueAsString(customerValidationRequest));
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq(EXCHANGE_NAME), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        //Assert
        String expectedResponse = MAPPER.writeValueAsString(new CustomerValidationResponse(customerValidationRequest.getCustomerId(), false));
        Assertions.assertThat(responseCaptor.getValue()).isEqualTo(expectedResponse);
    }

    @Test
    public void testOnMessageReceived_ValidationCustomer_InvalidUUID() throws IOException {
        //Arrange
        Logger logCapture = (Logger) LoggerFactory.getLogger(CustomerValidationReceiver.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        logCapture.addAppender(listAppender);

        //Act
        customerValidationReceiver.onMessageReceived(MessageRoutes.CUSTOMER_VALIDATE,"replyTo", "corrId", "123");

        //Assert
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertThat(logsList.get(3).getMessage()).contains("Could not process message. Cause:");
        Assertions.assertThat(logsList.get(3).getLevel()).isEqualTo(Level.ERROR);
    }
}