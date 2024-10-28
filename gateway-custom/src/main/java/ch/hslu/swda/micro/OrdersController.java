/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */

package ch.hslu.swda.micro;

import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import ch.hslu.swda.model.Order;
import ch.hslu.swda.model.OrderCreate;
import ch.hslu.swda.model.OrderUpdate;
import java.util.UUID;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api/v1")
@Tag(name = "Orders", description = "The Orders API")
public class OrdersController {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersController.class);

    /**
     * Get list of orders
     *
     * @return List&lt;Order&gt;
     */
    @Operation(
        operationId = "ordersGet",
        summary = "Get list of orders",
        responses = {
            @ApiResponse(responseCode = "200", description = "A list of orders", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            })
        }
    )
    @Get(uri="/orders")
    @Produces(value = {"application/json"})
    public Mono<List<Order>> ordersGet() {
        // TODO implement ordersGet();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }


    /**
     * Get an order by ID
     *
     * @param orderId  (required)
     * @return Order
     */
    @Operation(
        operationId = "ordersOrderIdGet",
        summary = "Get an order by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Order details", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "404", description = "Order not found")
        },
        parameters = {
            @Parameter(name = "orderId", required = true)
        }
    )
    @Get(uri="/orders/{orderId}")
    @Produces(value = {"application/json"})
    public Mono<Order> ordersOrderIdGet(
        @PathVariable(value="orderId") @NotNull UUID orderId
    ) {
        // TODO implement ordersOrderIdGet();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }


    /**
     * Update an order
     *
     * @param orderId  (required)
     * @param orderUpdate  (required)
     * @return Order
     */
    @Operation(
        operationId = "ordersOrderIdPut",
        summary = "Update an order",
        responses = {
            @ApiResponse(responseCode = "200", description = "Order updated", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "404", description = "Order not found")
        },
        parameters = {
            @Parameter(name = "orderId", required = true),
            @Parameter(name = "orderUpdate", required = true)
        }
    )
    @Put(uri="/orders/{orderId}")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<Order> ordersOrderIdPut(
        @PathVariable(value="orderId") @NotNull UUID orderId, 
        @Body @NotNull @Valid OrderUpdate orderUpdate
    ) {
        // TODO implement ordersOrderIdPut();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }


    /**
     * Create an order
     *
     * @param orderCreate  (required)
     * @return Order
     */
    @Operation(
        operationId = "ordersPost",
        summary = "Create an order",
        responses = {
            @ApiResponse(responseCode = "201", description = "Order created", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            })
        },
        parameters = {
            @Parameter(name = "orderCreate", required = true)
        }
    )
    @Post(uri="/orders")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<Order> ordersPost(
        @Body @NotNull @Valid OrderCreate orderCreate
    ) {
        // TODO implement ordersPost();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }

}
