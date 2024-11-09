package ch.hslu.swda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public final class CustomerValidationResponse {
    private final UUID customerId;
    private final boolean isValid;

    @JsonCreator
    public CustomerValidationResponse(
            @JsonProperty("customerId") UUID customerId,
            @JsonProperty("isValid") boolean isValid) {
        this.customerId = customerId;
        this.isValid = isValid;
    }

    public UUID getCustomerId() {
        return this.customerId;
    }

    public boolean getIsValid() {
        return this.isValid;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof CustomerValidationResponse that)
                &&
                (that.isValid == this.isValid)
                && Objects.equals(that.customerId, this.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, isValid);
    }

    @Override
    public String toString() {
        return "CustomerValidateResponse [" +
                "customerId=" + customerId +
                ", isValid=" + isValid +
                ']';
    }
}
