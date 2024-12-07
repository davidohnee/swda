/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */

package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.model.*;
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

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Controller("/api/v1")
@Tag(name = "Orders", description = "The Orders API")
public class OrdersController {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersController.class);
    private final BusConnector bus = new BusConnector();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String exchange;

    public OrdersController() throws IOException, TimeoutException {
        this.objectMapper.registerModule(new JavaTimeModule());
        this.bus.connect();
        RabbitMqConfig config = new RabbitMqConfig();
        this.exchange = config.getExchange();
    }

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
    @Get(uri = "/orders")
    @Produces(value = {"application/json"})
    public Mono<List<Order>> ordersGet() {
        return talkToBus(MessageRoutes.ORDER_GET_ENTITYSET, "", new TypeReference<List<Order>>() {
        }, "Error retrieving orders");
    }


    /**
     * Get an order by ID
     *
     * @param orderId (required)
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
    @Get(uri = "/orders/{orderId}")
    @Produces(value = {"application/json"})
    public Mono<Order> ordersOrderIdGet(
            @PathVariable(value = "orderId") @NotNull UUID orderId
    ) {
        return talkToBus(MessageRoutes.ORDER_GET_ENTITY, orderId, new TypeReference<Order>() {
        }, "Error retrieving order");
    }


    /**
     * Create an order
     *
     * @param orderCreate (required)
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
    @Post(uri = "/orders")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<Order> ordersPost(
            @Body @NotNull @Valid OrderCreate orderCreate
    ) {
        return talkToBus(MessageRoutes.ORDER_CREATE, orderCreate, new TypeReference<Order>() {
        }, "Error creating order");
    }

    /**
     * Update order items
     *
     * @param orderId         (required)
     * @param orderItemUpdate (required)
     * @return Order
     */
    @Operation(
            operationId = "ordersOrderIdPatchItems",
            summary = "Update order item quantities",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order items updated", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            },
            parameters = {
                    @Parameter(name = "orderId", required = true),
                    @Parameter(name = "orderItemUpdate", required = true)
            }
    )
    @Patch(uri = "/orders/{orderId}/items")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<Order> updateOrderItems(
            @PathVariable(value = "orderId") @NotNull UUID orderId,
            @Body @NotNull @Valid OrderItemUpdate orderItemUpdate
    ) {
        return talkToBus(MessageRoutes.ORDER_UPDATE_ITEMS, orderItemUpdate, new TypeReference<Order>() {
        }, "Error updating order items");
    }

    /**
     * Update order status
     *
     * @param orderId           (required)
     * @param orderStatusUpdate (required)
     * @return Order
     */
    @Operation(
            operationId = "ordersOrderIdPatchStatus",
            summary = "Update order status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order status updated", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            },
            parameters = {
                    @Parameter(name = "orderId", required = true),
                    @Parameter(name = "status", required = true)
            }
    )
    @Patch(uri = "/orders/{orderId}/status")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<Order> updateOrderStatus(
            @PathVariable(value = "orderId") @NotNull UUID orderId,
            @Body @NotNull @Valid OrderStatusUpdate status
    ) {
        return talkToBus(MessageRoutes.ORDER_UPDATE_STATUS, new OrderStatusUpdate(orderId, status.getStatus()), new TypeReference<Order>() {
        }, "Error updating order status");
    }

    /**
     * Update order customer
     *
     * @param orderId             (required)
     * @param orderCustomerUpdate (required)
     * @return Order
     */
    @Operation(
            operationId = "ordersOrderIdPatchCustomer",
            summary = "Update order customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order customer updated", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            },
            parameters = {
                    @Parameter(name = "orderId", required = true),
                    @Parameter(name = "orderCustomerUpdate", required = true)
            }
    )
    @Patch(uri = "/orders/{orderId}/customer")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<Order> updateOrderCustomer(
            @PathVariable(value = "orderId") @NotNull UUID orderId,
            @Body @NotNull @Valid OrderCustomerUpdate orderCustomerUpdate
    ) {
        return talkToBus(MessageRoutes.ORDER_UPDATE_CUSTOMER, orderCustomerUpdate, new TypeReference<Order>() {
        }, "Error updating order customer");
    }

    /**
     * Generic method to send a message to the bus and receive a response
     *
     * @param route         The route to send the message to
     * @param message       The message to send
     * @param responseClass The class of the response object
     * @param errorMessage  The error message to log in case of an error
     * @return A Mono with the response object
     */
    private <T, U> Mono<U> talkToBus(String route, T message, TypeReference<U> responseClass, String errorMessage) {
        try {
            String messageString = this.objectMapper.writeValueAsString(message);
            LOG.info("Sending message to route {} with message {}", route, message);
            String response = this.bus.talkSync(exchange, route, messageString);
            if (response == null) {
                return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process request"));
            }
            LOG.info("Received response: {}", response);
            return getMono(responseClass, response);
        } catch (Exception e) {
            LOG.error(errorMessage, e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage));
        }
    }

    private <U> Mono<U> getMono(TypeReference<U> responseClass, String response) {
        try {
            U responseObj = this.objectMapper.readValue(response, responseClass);
            return Mono.just(responseObj);
        } catch (IOException e) {
            LOG.warn("Response is not of expected type. Treating as error message: {}", response);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, response));
        }
    }
}
