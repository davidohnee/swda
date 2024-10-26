package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

class CustomerOrderTest {

        private Order order;
        private Customer customer;
        private Employee seller;

        @BeforeEach
        void setup() {
            Address address = new Address("Hauptstrasse", "1", "Luzern", 6000);
            customer = new Customer(1, "Hans", "Muster", address);
            seller = new Employee(1, "Hans", "Muster", Role.SALES);
            order = new OrderBuilder()
                    .setId(1)
                    .setTimestamp(new Date())
                    .setStatus(OrderStatus.NEW)
                    .setItems(Collections.emptyList())
                    .build();
        }


        @Test
        void testCreateCustomerOrder() {
            CustomerOrder customerOrder = new CustomerOrder(order, customer, seller);
            Order expectedOrder = new OrderBuilder()
                    .setId(1)
                    .setTimestamp(new Date())
                    .setStatus(OrderStatus.NEW)
                    .setItems(Collections.emptyList())
                    .build();
            Address address = new Address("Hauptstrasse", "1", "Luzern", 6000);
            Customer expectedCustomer = new Customer(1, "Hans", "Muster", address);
            Employee expectedSeller = new Employee(1, "Hans", "Muster", Role.SALES);
            Assertions.assertThat(customerOrder.getOrder()).isEqualTo(expectedOrder);
            Assertions.assertThat(customerOrder.getCustomer()).isEqualTo(expectedCustomer);
            Assertions.assertThat(customerOrder.getSeller()).isEqualTo(expectedSeller);

        }

        @Test
        void testEqualsContract() {
            EqualsVerifier.forClass(CustomerOrder.class)
                    .withOnlyTheseFields("order")
                    .verify();
        }

}