/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */
package ch.hslu.swda.common.entities;

import java.util.Objects;
import java.util.UUID;

//import ch.hslu.swda.common.entities.Order;
import com.fasterxml.jackson.annotation.*;
import java.time.OffsetDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.micronaut.core.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Notification
 */
@JsonPropertyOrder({
    Notification.JSON_PROPERTY_ID,
    Notification.JSON_PROPERTY_MESSAGE,
    Notification.JSON_PROPERTY_RECIPIENT,
    Notification.JSON_PROPERTY_SENT
})
@JsonTypeName("Notification")
@Introspected
public class Notification {
    public static final String JSON_PROPERTY_ID = "id";
    private UUID id;

    public static final String JSON_PROPERTY_MESSAGE = "message";
    private String message;

    public static final String JSON_PROPERTY_RECIPIENT = "recipient";
    private Recipient recipient;

    public static final String JSON_PROPERTY_SENT = "sent";
    private OffsetDateTime sent;

    public Notification(UUID id, String message, Recipient recipient, OffsetDateTime sent) {
        this.id = id;
        this.message = message;
        this.recipient = recipient;
        this.sent = sent;
    }

    public Notification id(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     */
    @NotNull
    @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public UUID getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setId(UUID id) {
        this.id = id;
    }

    public Notification message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Get message
     * @return message
     */
    @NotNull
    @Schema(name = "message", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_MESSAGE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getMessage() {
        return message;
    }

    @JsonProperty(JSON_PROPERTY_MESSAGE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMessage(String message) {
        this.message = message;
    }

    public Notification recipient(Recipient recipient) {
        this.recipient = recipient;
        return this;
    }

    /**
     * Get recipient
     * @return recipient
     */
    @Valid
    @NotNull
    @Schema(name = "recipient", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_RECIPIENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Recipient getRecipient() {
        return recipient;
    }

    @JsonProperty(JSON_PROPERTY_RECIPIENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Notification sent(OffsetDateTime sent) {
        this.sent = sent;
        return this;
    }

    /**
     * Get sent
     * @return sent
     */
    @NotNull
    @Schema(name = "sent", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(JSON_PROPERTY_SENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX")
    public OffsetDateTime getSent() {
        return sent;
    }

    @JsonProperty(JSON_PROPERTY_SENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX")
    public void setSent(OffsetDateTime sent) {
        this.sent = sent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Notification notification = (Notification) o;
        return Objects.equals(this.id, notification.id) &&
                Objects.equals(this.message, notification.message) &&
                Objects.equals(this.recipient, notification.recipient) &&
                Objects.equals(this.sent, notification.sent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, recipient, sent);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Notification {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    recipient: ").append(toIndentedString(recipient)).append("\n");
        sb.append("    sent: ").append(toIndentedString(sent)).append("\n");
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


