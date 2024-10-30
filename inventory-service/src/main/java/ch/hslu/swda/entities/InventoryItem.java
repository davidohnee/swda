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

    private Product product;
    private int quantity;

    /**
     * Constructor.
     *
     * @param product    product
     * @param quantity     quantity
     */
    public InventoryItem(final Product product, final int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * @return product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(final Product product) {
        this.product = product;
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
                && other.product.equals(this.product)
                && other.quantity == this.quantity;
    }

    /**
     * Hashcode based on fields. {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.product, this.quantity);
    }

    /**
     * String representation of month stat. {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "Inventory Item[productId=" + this.product + ", quantity='" + this.quantity + "]";
    }
}
