package ch.hslu.swda.micro;

import java.util.UUID;

/**
 * Interface for the product catalog service.
 */
public interface ProductCatalogService {

    /**
     * Checks if a product exists.
     *
     * @param productId The product id.
     * @return True if the product exists, false otherwise.
     */
    boolean productExists(UUID productId);
}
