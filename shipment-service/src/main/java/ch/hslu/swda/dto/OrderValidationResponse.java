package ch.hslu.swda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public final class OrderValidationResponse {
    private final UUID orderId;
    private final boolean isValid;

    @JsonCreator
    public OrderValidationResponse(
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("isValid") boolean isValid) {
        this.orderId = orderId;
        this.isValid = isValid;
    }

    public UUID getOrderId() {
        return this.orderId;
    }

    public boolean getIsValid() {
        return this.isValid;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof OrderValidationResponse that)
                &&
                (that.isValid == this.isValid)
                && Objects.equals(that.orderId, this.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, isValid);
    }

    @Override
    public String toString() {
        return "OrderValidateResponse [" +
                "orderId=" + orderId +
                ", isValid=" + isValid +
                ']';
    }
}
