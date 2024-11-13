package ch.hslu.swda.common.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class PersonTest {

    @Test
    void testCreatePerson() {
        UUID id = UUID.randomUUID();
        Person person = new Person(id, "Hans", "Muster");
        Assertions.assertThat(person.getId()).isEqualTo(id);
        Assertions.assertThat(person.getFirstName()).isEqualTo("Hans");
        Assertions.assertThat(person.getFamilyName()).isEqualTo("Muster");
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(Person.class)
                .withOnlyTheseFields("id")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}