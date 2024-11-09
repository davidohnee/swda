package ch.hslu.swda.dto;

import java.util.Objects;
import java.util.UUID;

public final class CustomerValidationRequest {
    private final UUID customerId;

    public CustomerValidationRequest(UUID customerId) {
        this.customerId = customerId;
    }

    public CustomerValidationRequest() {
        this(UUID.randomUUID());
    }

    public UUID getCustomerId() {
        return this.customerId;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof CustomerValidationRequest that)
                &&
                Objects.equals(that.customerId, this.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return "CustomerValidateRequest [" +
                "customerId=" + customerId +
                ']';
    }
}
