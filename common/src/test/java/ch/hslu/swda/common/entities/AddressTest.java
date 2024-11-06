package ch.hslu.swda.common.entities;

import ch.hslu.swda.common.entities.Address;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void testCreateAddress() {
        /*
        Address address = new Address("Hauptstrasse", "1", "Luzern", 6000);
        Assertions.assertThat(address.getStreet()).isEqualTo("Hauptstrasse");
        Assertions.assertThat(address.getHouseNumber()).isEqualTo("1");
        Assertions.assertThat(address.getCity()).isEqualTo("Luzern");
        Assertions.assertThat(address.getPostcode()).isEqualTo(6000);
         */
    }

    @Test
    void testEqualsContract() {
        //EqualsVerifier.forClass(Address.class).verify();
    }
}