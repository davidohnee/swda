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
    private int quantity;

    /**
     * Constructor.
     *
     * @param productId    product id
     * @param quantity     quantity
     */
    public InventoryItemUpdate(final int productId, final int quantity) {
        this.productId = productId;
        this.quantity = quantity;
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

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    /**
     * identical if same product id {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof InventoryItemUpdate other
                && Objects.equals(other.productId, this.productId);
    }

    /**
     * Hashcode based on product id. {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.productId);
    }

    /**
     * String representation. {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "Inventory Item Update[productId=" + this.productId + ", quantity='" + this.quantity + "]";
    }
}
