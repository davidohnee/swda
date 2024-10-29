package ch.hslu.swda.common.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class Customer extends Person {
    private final Address address;

    @JsonCreator
    public Customer(long id, String firstName, String lastName, Address address) {
        super(id, firstName, lastName);
        this.address = address;
    }

    public Address getAddress() {
        return this.address;
    }

    @Override
    public String toString() {
        return "Customer [" +
                "id='" + this.getId() + '\'' +
                ", first_name='" + this.getFirstName() + '\'' +
                ", last_name='" + this.getLastName() + '\'' +
                ", address='" + this.address + '\'' +
                ']';
    }
}
