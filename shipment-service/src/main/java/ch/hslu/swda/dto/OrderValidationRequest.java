package ch.hslu.swda.dto;

import java.util.Objects;
import java.util.UUID;

public final class OrderValidationRequest {
    private final UUID customerId;

    public OrderValidationRequest(UUID customerId) {
        this.customerId = customerId;
    }

    public OrderValidationRequest() {
        this(UUID.randomUUID());
    }

    public UUID geOrderId() {
        return this.customerId;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof OrderValidationRequest that)
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
