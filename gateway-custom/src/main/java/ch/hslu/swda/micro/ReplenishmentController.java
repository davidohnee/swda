/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */

package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.model.InventoryItem;
import ch.hslu.swda.model.InventoryItemUpdate;
import ch.hslu.swda.model.InventoryProductIdPatchRequest;
import ch.hslu.swda.model.ReplenishmentOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller("/api/v1")
@Tag(name = "Replenishment", description = "The Replenishment API")
public class ReplenishmentController {

    private static final Logger LOG = LoggerFactory.getLogger(ReplenishmentController.class);

    /**
     * Get inventory
     *
     * @return List&lt;ReplenishmentOrder&gt;
     */
    @Operation(
        operationId = "replenishmentGet",
        summary = "Get replenishment",
        responses = {
            @ApiResponse(responseCode = "200", description = "Replenishment details", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryItem.class))
            })
        }
    )
    @Get(uri="/replenishments")
    @Produces(value = {"application/json"})
    public Mono<List<ReplenishmentOrder>> inventoryGet() {
        try {
            String route = MessageRoutes.REPLENISHMENT_GET_ENTITYSET;
            String message = "";

            LOG.info("Sending message to route {}", route);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null) {
                return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve inventory"));
            }

            LOG.info("Received response: {}", response);
            ObjectMapper objectMapper = new ObjectMapper();
            List<ReplenishmentOrder> replenishments = objectMapper.readValue(response, new TypeReference<List<ReplenishmentOrder>>() {});

            return Mono.just(replenishments);
        } catch (Exception e) {
            LOG.error("Error retrieving inventory", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }
}
