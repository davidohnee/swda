package ch.hslu.swda.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void testCreateEmployee() {
        Employee employee = new Employee(1, "Hans", "Muster", Role.SALES);
        Assertions.assertThat(employee.getFirstName()).isEqualTo("Hans");
        Assertions.assertThat(employee.getLastName()).isEqualTo("Muster");
        Assertions.assertThat(employee.getRole()).isEqualTo(Role.SALES);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(Employee.class)
                .withOnlyTheseFields("id")
                .verify();
    }

}