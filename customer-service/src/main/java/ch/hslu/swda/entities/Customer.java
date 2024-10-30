package ch.hslu.swda.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public final class Customer extends Person {
    private final Address address;
    private final ContactInfo contactInfo;

    @JsonCreator
    public Customer(
            @JsonProperty("id") UUID id,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("familyName") String familyName,
            @JsonProperty("address") Address address,
            @JsonProperty("contactInfo") ContactInfo contactInfo
    ) {
        super(id, firstName, familyName);
        this.address = address;
        this.contactInfo = contactInfo;
    }

    public Address getAddress() {
        return this.address;
    }

    public ContactInfo getContactInfo() {
        return this.contactInfo;
    }

    @Override
    public String toString() {
        return "Customer [" +
                "id='" + this.getId().toString() + '\'' +
                ", first_name='" + this.getFirstName() + '\'' +
                ", family_name='" + this.getFamilyName() + '\'' +
                ", address='" + this.address + '\'' +
                ", contact_info'" + this.contactInfo + '\'' +
                ']';
    }
}

