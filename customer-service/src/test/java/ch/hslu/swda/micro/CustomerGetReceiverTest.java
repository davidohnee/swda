package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.common.database.CustomerDAO;
import ch.hslu.swda.common.entities.Customer;
import ch.hslu.swda.micro.receivers.CustomerGetReceiver;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class CustomerGetReceiverTest {

    private static final String EXCHANGE_NAME = "dummy-exchange";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BusConnector busConnector;
    private Customer customer;
    private CustomerDAO customerDAO;
    private CustomerGetReceiver customerGetReceiver;

    @BeforeEach
    public void setUp() {
        busConnector = mock(BusConnector.class);
        customerDAO = mock(CustomerDAO.class);
        customer = mock(Customer.class);
        customerGetReceiver = new CustomerGetReceiver(EXCHANGE_NAME, busConnector, customerDAO);
    }

    @Test
    public void testOnMessageReceived_GetEntity() throws IOException {
        //Arrange
        UUID customerId = UUID.randomUUID();
        when(customerDAO.read(customerId)).thenReturn(customer);

        //Act
        customerGetReceiver.onMessageReceived(MessageRoutes.CUSTOMER_GET_ENTITY, "replyTo", "corrId", MAPPER.writeValueAsString(customerId));
        ArgumentCaptor<String> reponseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq(EXCHANGE_NAME), eq("replyTo"), eq("corrId"), reponseCaptor.capture());

        //Assert
        String expectedResponse = MAPPER.writeValueAsString(customer);
        Assertions.assertThat(reponseCaptor.getValue()).isEqualTo(expectedResponse);
    }

    @Test
    public void testOnMessageReceived_GetEntity_NotFound() throws  IOException{
        //Arrange
        UUID customerId = UUID.randomUUID();
        when(customerDAO.read(customerId)).thenReturn(null);

        //Act
        customerGetReceiver.onMessageReceived(MessageRoutes.CUSTOMER_GET_ENTITY, "replyTo", "corrId", MAPPER.writeValueAsString(customerId));
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq(EXCHANGE_NAME), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        //Assert
        Assertions.assertThat(responseCaptor.getValue()).isEqualTo("");
    }

    @Test
    public  void testOnMessageReceived_GetEntity_InvalidUUID() throws IOException {
        //Arrange
        Logger logCapture = (Logger) LoggerFactory.getLogger(CustomerGetReceiver.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        logCapture.addAppender(listAppender);

        //Act
        customerGetReceiver.onMessageReceived(MessageRoutes.CUSTOMER_GET_ENTITY,"replyTo", "corrId", "123");

        //Assert
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertThat(logsList.get(2).getMessage()).contains("Could not process message. Cause:");
        Assertions.assertThat(logsList.get(2).getLevel()).isEqualTo(Level.ERROR);
    }

    @Test
    public void testOnMessageReceived_GetEntitySet() throws  IOException{
        //Arrange
        List<Customer> customers = List.of(customer, customer, customer);
        when(customerDAO.findAll()).thenReturn(customers);

        //Act
        customerGetReceiver.onMessageReceived(MessageRoutes.CUSTOMER_GET_ENTITYSET, "replyTo", "corrId", "");
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq(EXCHANGE_NAME), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        //Assert
        String expectedResponse = MAPPER.writeValueAsString(customers);
        Assertions.assertThat(responseCaptor.getValue()).isEqualTo(expectedResponse);
    }
}