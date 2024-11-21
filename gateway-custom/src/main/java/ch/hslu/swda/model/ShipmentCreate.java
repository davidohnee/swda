/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */
package ch.hslu.swda.model;

import java.util.Objects;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.*;

import jakarta.validation.constraints.*;
import io.micronaut.core.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ShipmentCreate
 */
@JsonPropertyOrder({
    ShipmentCreate.JSON_PROPERTY_ORDER_ID,
    ShipmentCreate.JSON_PROPERTY_DEPARTURE,
    ShipmentCreate.JSON_PROPERTY_ESTIMATED_ARRIVAL
})
@JsonTypeName("ShipmentCreate")
@Introspected
public class ShipmentCreate {
    public static final String JSON_PROPERTY_ORDER_ID = "orderId";
    private UUID orderId;

    public static final String JSON_PROPERTY_DEPARTURE = "departure";
    private OffsetDateTime departure;

    public static final String JSON_PROPERTY_ESTIMATED_ARRIVAL = "estimatedArrival";
    private OffsetDateTime estimatedArrival;

    public ShipmentCreate(UUID orderId, OffsetDateTime departure, OffsetDateTime estimatedArrival) {
        this.orderId = orderId;
        this.departure = departure;
        this.estimatedArrival = estimatedArrival;
    }

    public ShipmentCreate() {}

    public ShipmentCreate orderId(UUID orderId) {
        this.orderId = orderId;
        return this;
    }

    /**
     * The ID of the order to be shipped
     * @return orderId
     */
    @NotNull
    @Schema(name = "orderId", description = "The ID of the order to be shipped", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_ORDER_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public UUID getOrderId() {
        return orderId;
    }

    @JsonProperty(JSON_PROPERTY_ORDER_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public ShipmentCreate departure(OffsetDateTime departure) {
        this.departure = departure;
        return this;
    }

    /**
     * The date and time when the shipment will depart
     * @return departure
     */
    @NotNull
    @Schema(name = "departure", description = "The date and time when the shipment will depart", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_DEPARTURE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX")
    public OffsetDateTime getDeparture() {
        return departure;
    }

    @JsonProperty(JSON_PROPERTY_DEPARTURE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX")
    public void setDeparture(OffsetDateTime departure) {
        this.departure = departure;
    }

    public ShipmentCreate estimatedArrival(OffsetDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
        return this;
    }

    /**
     * The estimated date and time of arrival for the shipment
     * @return estimatedArrival
     */
    @NotNull
    @Schema(name = "estimatedArrival", description = "The estimated date and time of arrival for the shipment", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_ESTIMATED_ARRIVAL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX")
    public OffsetDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    @JsonProperty(JSON_PROPERTY_ESTIMATED_ARRIVAL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX")
    public void setEstimatedArrival(OffsetDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShipmentCreate shipmentCreate = (ShipmentCreate) o;
        return Objects.equals(this.orderId, shipmentCreate.orderId) &&
                Objects.equals(this.departure, shipmentCreate.departure) &&
                Objects.equals(this.estimatedArrival, shipmentCreate.estimatedArrival);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, departure, estimatedArrival);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ShipmentCreate {\n");
        sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
        sb.append("    departure: ").append(toIndentedString(departure)).append("\n");
        sb.append("    estimatedArrival: ").append(toIndentedString(estimatedArrival)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}