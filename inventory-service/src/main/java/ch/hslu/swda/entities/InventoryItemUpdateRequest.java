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
import java.util.UUID;

/**
 * Tuple for Month and Number of students.
 */
public final class InventoryItemUpdateRequest {

    final private UUID productId;
    final private int count;

    /**
     * Constructor.
     *
     * @param productId    product id
     * @param count     quantity
     */
    public InventoryItemUpdateRequest(final UUID productId, final int count) {
        this.productId = productId;
        this.count = count;
    }

    public InventoryItemUpdateRequest() {
        this(UUID.randomUUID(), 0);
    }

    /**
     * @return product id
     */
    public UUID getProductId() {
        return productId;
    }

    /**
     * @return the quantity
     */
    public int getCount() {
        return count;
    }


    /**
     * identical if same product id and count {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof InventoryItemUpdateRequest other
                && this.count == other.count
                && Objects.equals(other.productId, this.productId);
    }

    /**
     * Hashcode based on product id. {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.productId, this.count);
    }

    /**
     * String representation. {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "Inventory Item[productId=" + this.productId + ", quantity='" + this.count + "]";
    }
}
