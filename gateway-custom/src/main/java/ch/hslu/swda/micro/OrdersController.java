/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */

package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

import java.io.IOException;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.TimeoutException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.scheduler.Schedulers;

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
        try {
            String route = MessageRoutes.ORDER_GET_ENTITYSET;
            String message = "";

            LOG.info("Sending message to route {}", route);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null) {
                return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve orders"));
            }

            LOG.info("Received response: {}", response);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            List<Order> orders = objectMapper.readValue(response, new TypeReference<List<Order>>() {});

            return Mono.just(orders);
        } catch (Exception e) {
            LOG.error("Error retrieving orders", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
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
        try {
            String route = MessageRoutes.ORDER_GET_ENTITY;

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String message = objectMapper.writeValueAsString(orderId);

            LOG.info("Sending message to route {} with orderId {}", route, orderId);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null) {
                return Mono.error(new HttpStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            }

            LOG.info("Received response: {}", response);
            Order order = objectMapper.readValue(response, Order.class);

            return Mono.just(order);
        } catch (Exception e) {
            LOG.error("Error retrieving order", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
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
        try {
            String route = MessageRoutes.ORDER_CREATE;

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String message = objectMapper.writeValueAsString(orderCreate);

            LOG.info("Sending message to route {} with orderCreate {}", route, orderCreate);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null || response.equals("Error processing request")) {
                return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create order"));
            }

            LOG.info("Received response: {}", response);
            Order order = objectMapper.readValue(response, Order.class);

            return Mono.just(order);
        } catch (Exception e) {
            LOG.error("Error creating order", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }
}
