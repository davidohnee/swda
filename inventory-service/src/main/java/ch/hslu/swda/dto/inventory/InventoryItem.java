/*
 * Copyright 2024 Roland Christen, HSLU Informatik, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hslu.swda.dto.inventory;

import ch.hslu.swda.entities.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;


/**
 * Tuple for Month and Number of students.
 */
public class InventoryItem {
    public final static int DEFAULT_REPLENISHMENT_THRESHOLD = 10;

    final private Product product;
    private int replenishmentThreshold;
    protected int quantity;

    /**
     * Constructor.
     *
     * @param product    product
     * @param replenishmentThreshold replenishmentThreshold
     * @param quantity     quantity
     */
    public InventoryItem(final Product product, int replenishmentThreshold, final int quantity) {
        this.product = product;
        this.replenishmentThreshold = replenishmentThreshold;
        this.quantity = quantity;
    }

    /**
     * Constructor.
     *
     * @param product product
     * @param quantity quantity
     */

    public InventoryItem(final Product product, final int quantity) {
        this(product, DEFAULT_REPLENISHMENT_THRESHOLD, quantity);
    }

    /**
     * @return product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return the replenishmentThreshold
     */
    public int getReplenishmentThreshold() {
        return replenishmentThreshold;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    /**
     * @param replenishmentThreshold the replenishmentThreshold to set
     */
    public void setReplenishmentThreshold(final int replenishmentThreshold) {
        this.replenishmentThreshold = replenishmentThreshold;
    }

    @JsonIgnore
    public int getReplenishmentAmount() {
        return 5 * this.replenishmentThreshold;
    }

    /**
     * identical if same product {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof InventoryItem other
                && Objects.equals(other.product, this.product);
    }

    /**
     * Hashcode based on field product. {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.product);
    }

    /**
     * String representation. {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "InventoryItem[" +
                "product=" + this.product +
                ", quantity='" + this.quantity +
                ", replenishmentThreshold='" + this.replenishmentThreshold +
                "]";
    }
}
