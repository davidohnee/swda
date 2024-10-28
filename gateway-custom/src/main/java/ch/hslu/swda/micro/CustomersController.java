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
import ch.hslu.swda.model.Customer;
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
     * @param customerId  (required)
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
    @Get(uri="/customers/{customerId}")
    @Produces(value = {"application/json"})
    public Mono<Customer> customersCustomerIdGet(
        @PathVariable(value="customerId") @NotNull UUID customerId
    ) {
        // TODO implement customersCustomerIdGet();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
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
    @Get(uri="/customers")
    @Produces(value = {"application/json"})
    public Mono<List<Customer>> customersGet() {
        // TODO implement customersGet();
        return Mono.error(new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, null));
    }

}
