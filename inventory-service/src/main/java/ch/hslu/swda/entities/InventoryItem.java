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
package ch.hslu.swda.entities;

import java.util.Objects;

/**
 * Tuple for Month and Number of students.
 */
public final class InventoryItem {

    private long productId;
    private int quantity;

    /**
     * Default Constructor.
     */
    public InventoryItem() {
        this(0, 0);
    }


    /**
     * Constructor.
     *
     * @param month        month number, zero-based.
     * @param studentCount number of students born in this month.
     */
    public InventoryItem(final long productId, final int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * @return product id
     */
    public long getProductId() {
        return productId;
    }

    /**
     * @param productId the product id to set
     */
    public void setProductId(final long productId) {
        this.productId = productId;
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
     * identical if same month and student count {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof InventoryItem other
                && other.productId == this.productId
                && other.quantity == this.quantity;
    }

    /**
     * Hashcode based on fields. {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.productId, this.quantity);
    }

    /**
     * String representation of month stat. {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "Inventory Item[productId=" + this.productId + ", quantity='" + this.quantity + "]";
    }
}
