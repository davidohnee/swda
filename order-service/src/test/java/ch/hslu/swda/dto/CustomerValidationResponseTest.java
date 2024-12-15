package ch.hslu.swda.dto;

import ch.hslu.swda.micro.customer.dto.CustomerValidationResponse;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CustomerValidationResponseTest {

    @Test
    void testCustomerValidateResponse() {
        UUID customerId = UUID.randomUUID();
        boolean isValid = true;
        CustomerValidationResponse response = new CustomerValidationResponse(customerId, isValid);
        Assertions.assertThat(response.getCustomerId()).isEqualTo(customerId);
        Assertions.assertThat(response.getIsValid()).isEqualTo(isValid);
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(CustomerValidationResponse.class).verify();
    }

}