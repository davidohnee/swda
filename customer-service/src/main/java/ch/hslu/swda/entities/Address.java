package ch.hslu.swda.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Address {
    private final String streetName;
    private final String streetNumber;
    private final String townName;
    private final int plz;

    @JsonCreator
    public Address(
            @JsonProperty("streetName") String streetName,
            @JsonProperty("streetNumber") String streetNumber,
            @JsonProperty("townName") String townName,
            @JsonProperty("plz") int plz
    ) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.townName = townName;
        this.plz = plz;
    }

    public String getStreetName() {
        return this.streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getTownName() {
        return this.townName;
    }

    public int getPlz() {
        return this.plz;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof Address address)
                &&
                Objects.equals(address.streetName, this.streetName)
                && Objects.equals(address.streetNumber, this.streetNumber)
                && Objects.equals(address.townName, this.townName)
                && Objects.equals(address.plz, this.plz);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.streetName, this.townName, plz);
    }

    @Override
    public String toString() {
        return "Address [" +
                "street_name='" + this.streetName + '\'' +
                ", townName='" + this.townName + '\'' +
                ", plz='" + this.plz + '\'' +
                ']';
    }
}
