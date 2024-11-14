/*
 * Order Service G09
 * API designed by team G09 for the HS24 SWDA course at HSLU
 *
 * The version of the OpenAPI document: 1.0.0
 */

package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
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

@Controller("/api/v1")
@Tag(name = "Customers", description = "The Customers API")
public class CustomersController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomersController.class);

    /**
     * Get a specific customer
     *
     * @param customerId (required)
     * @return Customer
     */
    @Operation(
            operationId = "customersCustomerIdGet",
            summary = "Get a specific customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer details", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            },
            parameters = {
                    @Parameter(name = "customerId", required = true)
            }
    )
    @Get(uri = "/customers/{customerId}")
    @Produces(value = {"application/json"})
    public Mono<Customer> customersCustomerIdGet(
            @PathVariable(value = "customerId") @NotNull UUID customerId
    ) {
        try {
            String route = MessageRoutes.CUSTOMER_GET_ENTITY;

            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(customerId);

            LOG.info("Sending message to route {} with customerId {}", route, customerId);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null || response.isEmpty()) {
                return Mono.error(new HttpStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
            }

            LOG.info("Received response: {}", response);
            Customer customer = objectMapper.readValue(response, Customer.class);

            return Mono.just(customer);
        } catch (Exception e) {
            LOG.error("Error retrieving customer", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }


    /**
     * Get all customers
     *
     * @return List&lt;Customer&gt;
     */
    @Operation(
            operationId = "customersGet",
            summary = "Get all customers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of customers", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))
                    })
            }
    )
    @Get(uri = "/customers")
    @Produces(value = {"application/json"})
    public Mono<List<Customer>> customersGet() {
        try {
            String route = MessageRoutes.CUSTOMER_GET_ENTITYSET;
            String message = "";

            LOG.info("Sending message to route {}", route);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null) {
                return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve customers"));
            }

            LOG.info("Received response: {}", response);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            List<Customer> customers = objectMapper.readValue(response, new TypeReference<List<Customer>>() {
            });

            return Mono.just(customers);
        } catch (Exception e) {
            LOG.error("Error retrieving customers", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    /**
     * Create a new customer
     *
     * @param customerCreate (required)
     * @return Customer
     */
    @Operation(
        operationId = "customersPost",
        summary = "Create a new customer",
        responses = {
            @ApiResponse(responseCode = "201", description = "Customer created", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        parameters = {
            @Parameter(name = "customerCreate", required = true)
        }
    )
    @Post(uri = "/customers")
    @Produces(value = {"application/json"})
    @Consumes(value = {"application/json"})
    public Mono<Customer> customersPost(
            @Body @NotNull @Valid CustomerCreate customerCreate
    ) {
        try {
            String route = MessageRoutes.CUSTOMER_CREATE;

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String message = objectMapper.writeValueAsString(customerCreate);

            LOG.info("Sending message to route {} with orderCreate {}", route, customerCreate);

            RabbitMqConfig config = new RabbitMqConfig();
            String exchange = config.getExchange();

            BusConnector bus = new BusConnector();
            bus.connect();

            String response = bus.talkSync(exchange, route, message);

            if (response == null || response.equals("Error processing request")) {
                return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create customer"));
            }

            LOG.info("Received response: {}", response);
            Customer customer = objectMapper.readValue(response, Customer.class);

            return Mono.just(customer);
        } catch (Exception e) {
            LOG.error("Error creating customer", e);
            return Mono.error(new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

}
