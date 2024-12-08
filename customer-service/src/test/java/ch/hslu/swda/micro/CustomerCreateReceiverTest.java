package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.database.CustomerDAO;
import ch.hslu.swda.common.entities.Address;
import ch.hslu.swda.common.entities.ContactInfo;
import ch.hslu.swda.common.entities.Customer;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.common.entities.CustomerCreate;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class CustomerCreateReceiverTest {

    private static final String EXCHANGE_NAME = "dummy-exchange";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BusConnector busConnector;
    private Customer customer;
    private CustomerDAO customerDAO;
    private CustomerCreateReceiver customerCreateReceiver;

    @BeforeEach
    public void setUp() {
        busConnector = mock(BusConnector.class);
        customerDAO = mock(CustomerDAO.class);
        customer = mock(Customer.class);
        customerCreateReceiver = new CustomerCreateReceiver(EXCHANGE_NAME, busConnector, customerDAO);
    }

    @Test
    public void testOnMessageReceived_CreateCustomer() throws IOException {
        //Arrange
        CustomerCreate customerCreate = new CustomerCreate("Peter","Pan", new Address("Teststrasse ", "4", "6003", "Luzern"), new ContactInfo().email("Peter.Pan@Proton.me").phone("+41 123 45 67"));
        String customerString = MAPPER.writeValueAsString(customerCreate);
        ObjectMapper customerMapper = new ObjectMapper();

        //Act
        customerCreateReceiver.onMessageReceived(MessageRoutes.CUSTOMER_CREATE, "replyTo", "corrId", customerString);
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(busConnector).reply(eq(EXCHANGE_NAME), eq("replyTo"), eq("corrId"), responseCaptor.capture());

        //Assert
        Customer responseCustomer = customerMapper.readValue(responseCaptor.getValue(), Customer.class);
        Customer expectedCustomer = new Customer(responseCustomer.getCustomerId(),customerCreate.getFirstName(),customerCreate.getFamilyName(), customerCreate.getAddress(),customerCreate.getContactInfo(), new ObjectId());
        String expectedCustomerString = customerMapper.writeValueAsString(expectedCustomer);
        Assertions.assertThat(responseCaptor.getValue()).isEqualTo(expectedCustomerString);
    }

    @Test
    public  void testOnMessageReceived_InvalidCustomer() throws IOException {
        //Arrange
        Logger logCapture = (Logger) LoggerFactory.getLogger(CustomerCreateReceiver.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        logCapture.addAppender(listAppender);

        //Act
        customerCreateReceiver.onMessageReceived(MessageRoutes.CUSTOMER_CREATE,"replyTo", "corrId", "123");

        //Assert
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertThat(logsList.get(2).getMessage()).contains("Failed to persist customer Cause:");
        Assertions.assertThat(logsList.get(2).getLevel()).isEqualTo(Level.ERROR);
    }
}