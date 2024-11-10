/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */

package ch.hslu.swda.common.entities;

import java.util.Objects;
import com.fasterxml.jackson.annotation.*;

import jakarta.validation.constraints.*;
import io.micronaut.core.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contact information (to be defined)
 */
@Schema(name = "ContactInfo", description = "Contact information (to be defined)")
@JsonPropertyOrder({
    ContactInfo.JSON_PROPERTY_EMAIL,
    ContactInfo.JSON_PROPERTY_PHONE
})
@JsonTypeName("ContactInfo")
@Introspected
public class ContactInfo {
    public static final String JSON_PROPERTY_EMAIL = "email";
    private String email;

    public static final String JSON_PROPERTY_PHONE = "phone";
    private String phone;

    public ContactInfo() {}

    public ContactInfo(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    public ContactInfo email(String email) {
        this.email = email;
        return this;
    }

    /**
     * Get email
     * @return email
     */
    @Nullable
    @Email
    @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty(JSON_PROPERTY_EMAIL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getEmail() {
        return email;
    }

    @JsonProperty(JSON_PROPERTY_EMAIL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEmail(String email) {
        this.email = email;
    }

    public ContactInfo phone(String phone) {
        this.phone = phone;
        return this;
    }

    /**
     * Get phone
     * @return phone
     */
    @Nullable
    @Schema(name = "phone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty(JSON_PROPERTY_PHONE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPhone() {
        return phone;
    }

    @JsonProperty(JSON_PROPERTY_PHONE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContactInfo contactInfo = (ContactInfo) o;
        return Objects.equals(this.email, contactInfo.email) &&
                Objects.equals(this.phone, contactInfo.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phone);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ContactInfo {\n");
        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
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

