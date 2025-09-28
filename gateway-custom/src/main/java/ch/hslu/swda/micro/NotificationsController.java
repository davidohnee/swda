/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */
package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.model.Notification;
import ch.hslu.swda.model.Shipment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.http.annotation.*;
import io.micronaut.serde.annotation.SerdeImport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

import ch.hslu.swda.common.routing.MessageRoutes;

import java.util.UUID;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api/v1")
@Tag(name = "Notifications", description = "The Notifications API")
public class NotificationsController {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationsController.class);

    /**
     * Get list of notifications
     *
     * @return List&lt;Notification&gt;
     */
    @Operation(
        operationId = "notificationsGet",
        summary = "Get list of notifications",
        responses = {
            @ApiResponse(responseCode = "200", description = "A list of notifications", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class))
            })
        }
    )
    @Get(uri="/notifications")
    @Produces(value = {"application/json"})
    public Mono<List<Notification>> notificationsGet() {
        try {
            String route = MessageRoutes.NOTIFICATION_GET_ENTITYSET;
            String message = "";

            LOG.info("Sending message to route {}", route);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null) {
                return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve notifications"));
            }

            LOG.info("Received response: {}", response);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            List<Notification> notifications = objectMapper.readValue(response, new TypeReference<List<Notification>>() {});

            return Mono.just(notifications);
        } catch (Exception e) {
            LOG.error("Error retrieving notifications", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }


    /**
     * Get a notification by ID
     *
     * @param notificationId  (required)
     * @return Notification
     */
    @Operation(
        operationId = "notificationsNotificationIdGet",
        summary = "Get a notification by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Notification details", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class))
            }),
            @ApiResponse(responseCode = "404", description = "Notification not found")
        },
        parameters = {
            @Parameter(name = "notificationId", required = true)
        }
    )
    @Get(uri="/notifications/{notificationId}")
    @Produces(value = {"application/json"})
    public Mono<Notification> notificationsNotificationIdGet(
            @PathVariable(value="notificationId") @NotNull UUID notificationId
    ) {
        try {
            String route = MessageRoutes.NOTIFICATION_GET_ENTITY;

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String message = objectMapper.writeValueAsString(notificationId);

            LOG.info("Sending message to route {} with notificationId {}", route, notificationId);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null || response.isEmpty()) {
                return Mono.error(new HttpStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
            }

            LOG.info("Received response: {}", response);
            Notification notification = objectMapper.readValue(response, Notification.class);

            return Mono.just(notification);
        } catch (Exception e) {
            LOG.error("Error retrieving notification", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

}
