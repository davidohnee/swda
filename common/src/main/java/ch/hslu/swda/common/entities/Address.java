/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */

package ch.hslu.swda.common.entities;

import java.util.Objects;
import com.fasterxml.jackson.annotation.*;

import io.micronaut.core.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Address
 */
@JsonPropertyOrder({
    Address.JSON_PROPERTY_STREET_NAME,
    Address.JSON_PROPERTY_STREET_NUMBER,
    Address.JSON_PROPERTY_PLZ,
    Address.JSON_PROPERTY_TOWN_NAME
})
@JsonTypeName("Address")
@Introspected
public class Address {
    public static final String JSON_PROPERTY_STREET_NAME = "streetName";
    private String streetName;

    public static final String JSON_PROPERTY_STREET_NUMBER = "streetNumber";
    private String streetNumber;

    public static final String JSON_PROPERTY_PLZ = "plz";
    private String plz;

    public static final String JSON_PROPERTY_TOWN_NAME = "townName";
    private String townName;

    public Address(String streetName, String streetNumber, String plz, String townName) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.plz = plz;
        this.townName = townName;
    }

    public Address() {}

    public Address streetName(String streetName) {
        this.streetName = streetName;
        return this;
    }

    /**
     * Get streetName
     * @return streetName
     */
    @NotNull
    @Schema(name = "streetName", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_STREET_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getStreetName() {
        return streetName;
    }

    @JsonProperty(JSON_PROPERTY_STREET_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Address streetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        return this;
    }

    /**
     * Get streetNumber
     * @return streetNumber
     */
    @NotNull
    @Schema(name = "streetNumber", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_STREET_NUMBER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getStreetNumber() {
        return streetNumber;
    }

    @JsonProperty(JSON_PROPERTY_STREET_NUMBER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public Address plz(String plz) {
        this.plz = plz;
        return this;
    }

    /**
     * Get plz
     * @return plz
     */
    @NotNull
    @Schema(name = "plz", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_PLZ)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getPlz() {
        return plz;
    }

    @JsonProperty(JSON_PROPERTY_PLZ)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setPlz(String plz) {
        this.plz = plz;
    }

    public Address townName(String townName) {
        this.townName = townName;
        return this;
    }

    /**
     * Get townName
     * @return townName
     */
    @NotNull
    @Schema(name = "townName", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_TOWN_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getTownName() {
        return townName;
    }

    @JsonProperty(JSON_PROPERTY_TOWN_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTownName(String townName) {
        this.townName = townName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(this.streetName, address.streetName) &&
                Objects.equals(this.streetNumber, address.streetNumber) &&
                Objects.equals(this.plz, address.plz) &&
                Objects.equals(this.townName, address.townName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetName, streetNumber, plz, townName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Address {\n");
        sb.append("    streetName: ").append(toIndentedString(streetName)).append("\n");
        sb.append("    streetNumber: ").append(toIndentedString(streetNumber)).append("\n");
        sb.append("    plz: ").append(toIndentedString(plz)).append("\n");
        sb.append("    townName: ").append(toIndentedString(townName)).append("\n");
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

