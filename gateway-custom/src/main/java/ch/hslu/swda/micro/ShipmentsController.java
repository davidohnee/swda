/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
*/

package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.entities.Shipment;
import ch.hslu.swda.common.entities.ShipmentCreate;
import ch.hslu.swda.model.CustomerCreate;
import ch.hslu.swda.model.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import ch.hslu.swda.model.Customer;

import ch.hslu.swda.common.routing.MessageRoutes;

import java.util.UUID;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Shipments", description = "The Shipments API")
public class ShipmentsController {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentsController.class);

    /**
     * Get all shipments
     *
     * @return List&lt;Shipment&gt;
     */
    @Operation(
        operationId = "shipmentsGet",
        summary = "Get all shipments",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of shipments", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Shipment.class))
            })
        }
    )
    @Get(uri="/shipments")
    @Produces(value = {"application/json"})
    public Mono<List<Shipment>> shipmentsGet() {
        // TODO implement shipmentsGet();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }


    /**
     * Get a specific shipment
     *
     * @param shipmentId  (required)
     * @return Shipment
     */
    @Operation(
        operationId = "shipmentsShipmentIdGet",
        summary = "Get a specific shipment",
        responses = {
            @ApiResponse(responseCode = "200", description = "Shipment details", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Shipment.class))
            }),
            @ApiResponse(responseCode = "404", description = "Shipment not found")
        },
        parameters = {
            @Parameter(name = "shipmentId", required = true)
        }
    )
    @Get(uri="/shipments/{shipmentId}")
    @Produces(value = {"application/json"})
    public Mono<Shipment> shipmentsShipmentIdGet(
            @PathVariable(value="shipmentId") @NotNull UUID shipmentId
    ) {
        // TODO implement shipmentsShipmentIdGet();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }


    /**
     * Create a new shipment
     *
     * @param shipmentCreate  (required)
     * @return Shipment
     */
    @Operation(
        operationId = "shipmentsPost",
        summary = "Create a new shipment",
        responses = {
            @ApiResponse(responseCode = "201", description = "Shipment created successfully", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Shipment.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Order not found")
        },
        parameters = {
            @Parameter(name = "shipmentCreate", required = true)
        }
    )
    @Post(uri="/shipments")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<Shipment> shipmentsPost(
            @Body @NotNull @Valid ShipmentCreate shipmentCreate
    ) {
        // TODO implement shipmentsPost();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }

}
