/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */

package ch.hslu.swda.micro;

import io.micronaut.http.annotation.*;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import ch.hslu.swda.model.InventoryItem;
import ch.hslu.swda.model.InventoryProductIdPatchRequest;
import java.util.UUID;
import java.util.List;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api/v1")
@Tag(name = "Inventory", description = "The Inventory API")
public class InventoryController {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryController.class);

    /**
     * Get inventory
     *
     * @return List&lt;InventoryItem&gt;
     */
    @Operation(
        operationId = "inventoryGet",
        summary = "Get inventory",
        responses = {
            @ApiResponse(responseCode = "200", description = "Inventory details", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryItem.class))
            })
        }
    )
    @Get(uri="/inventory")
    @Produces(value = {"application/json"})
    public Mono<List<InventoryItem>> inventoryGet() {
        // TODO implement inventoryGet();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }


    /**
     * Update product count in inventory
     *
     * @param productId  (required)
     * @param inventoryProductIdPatchRequest  (required)
     * @return InventoryItem
     */
    @Operation(
        operationId = "inventoryProductIdPatch",
        summary = "Update product count in inventory",
        responses = {
            @ApiResponse(responseCode = "200", description = "Inventory updated", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryItem.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product not found")
        },
        parameters = {
            @Parameter(name = "productId", required = true),
            @Parameter(name = "inventoryProductIdPatchRequest", required = true)
        }
    )
    @Patch(uri="/inventory/{productId}")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<InventoryItem> inventoryProductIdPatch(
        @PathVariable(value="productId") @NotNull UUID productId, 
        @Body @NotNull @Valid InventoryProductIdPatchRequest inventoryProductIdPatchRequest
    ) {
        // TODO implement inventoryProductIdPatch();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }

}
