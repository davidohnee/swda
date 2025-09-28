package ch.hslu.swda.dto;

import ch.hslu.swda.micro.customer.dto.CustomerValidationRequest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CustomerValidationRequestTest {

    @Test
    void testCustomerValidateRequest() {
        UUID customerId = UUID.randomUUID();
        CustomerValidationRequest request = new CustomerValidationRequest(customerId);
        Assertions.assertThat(request.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(CustomerValidationRequest.class).verify();
    }

}