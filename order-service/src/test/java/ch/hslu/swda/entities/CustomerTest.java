package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void testCreateCustomer() {
        Address address = new Address("Hauptstrasse", "1", "Luzern", 6000);
        Customer customer = new Customer(1, "Hans", "Muster", address);
        Assertions.assertThat(customer.getFirstName()).isEqualTo("Hans");
        Assertions.assertThat(customer.getLastName()).isEqualTo("Muster");
        Assertions.assertThat(customer.getAddress()).isEqualTo(address);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(Customer.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}