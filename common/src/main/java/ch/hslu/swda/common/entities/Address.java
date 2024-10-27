package ch.hslu.swda.common.entities;

import java.util.Objects;

public class Address {
    private final String street;
    private final String houseNumber;
    private final String city;
    private final int postcode;

    public Address(String street, String houseNumber, String city, int postcode) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
        this.postcode = postcode;
    }

    public String getStreet() {
        return this.street;
    }

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public String getCity() {
        return this.city;
    }

    public int getPostcode() {
        return this.postcode;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof Address address)
                &&
                Objects.equals(address.street, this.street)
                && Objects.equals(address.houseNumber, this.houseNumber)
                && Objects.equals(address.city, this.city)
                && Objects.equals(address.postcode, this.postcode);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.street, this.houseNumber, this.city, postcode);
    }

    @Override
    public String toString() {
        return "Address [" +
                "street='" + this.street + '\'' +
                ", house_number='" + this.houseNumber + '\'' +
                ", city='" + this.city + '\'' +
                ", postcode='" + this.postcode + '\'' +
                ']';
    }
}
