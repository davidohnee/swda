package ch.hslu.swda.common.entities;

import ch.hslu.swda.common.entities.Person;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PersonTest {

    @Test
    void testCreatePerson() {
        Person person = new Person(1, "Hans", "Muster");
        Assertions.assertThat(person.getId()).isEqualTo(1);
        Assertions.assertThat(person.getFirstName()).isEqualTo("Hans");
        Assertions.assertThat(person.getLastName()).isEqualTo("Muster");
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(Person.class)
                .withOnlyTheseFields("id")
                .verify();
    }

}