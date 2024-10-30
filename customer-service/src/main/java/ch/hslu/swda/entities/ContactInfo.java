package ch.hslu.swda.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ContactInfo {
    private final String email;
    private final String phone;

    @JsonCreator
    public ContactInfo(
            @JsonProperty("email") String email,
            @JsonProperty("phone") String phone
    ) {
        this.email = email;
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof ContactInfo contactInfo)
                && Objects.equals(contactInfo.email, this.email)
                && Objects.equals(contactInfo.phone, this.phone);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.email, this.phone);
    }

    @Override
    public String toString() {
        return "ContactInfo [" +
                "email='" + this.email + '\'' +
                ", phone='" + this.phone + '\'' +
                ']';
    }
}
