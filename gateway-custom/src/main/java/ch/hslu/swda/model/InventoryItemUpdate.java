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
package ch.hslu.swda.model;

import java.util.Objects;

/**
 * Tuple for Month and Number of students.
 */
public final class InventoryItemUpdate {

    final private int productId;
    final private int quantity;
    final private Integer replenishmentThreshold;

    /**
     * Constructor.
     *
     * @param productId    product id
     * @param quantity     quantity
     */
    public InventoryItemUpdate(final int productId, final int quantity, Integer replenishmentThreshold) {
        this.productId = productId;
        this.quantity = quantity;
        this.replenishmentThreshold = replenishmentThreshold;
    }

    public InventoryItemUpdate() {
        this(0, 0, 0);
    }

    /**
     * @return product id
     */
    public int getProductId() {
        return productId;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    public Integer getReplenishmentThreshold() {
        return replenishmentThreshold;
    }

    /**
     * identical if all properties are equal {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof InventoryItemUpdate other
                && this.quantity == other.quantity
                && this.productId == other.productId
                && Objects.equals(this.replenishmentThreshold, other.replenishmentThreshold);
    }

    /**
     * Hashcode based on all properties. {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.productId, this.quantity, this.replenishmentThreshold);
    }

    /**
     * String representation. {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "InventoryUpdateRequest[" +
                "productId=" + this.productId +
                ", quantity='" + this.quantity +
                ", replenishmentThreshold='" + this.replenishmentThreshold +
                "]";
    }
}
