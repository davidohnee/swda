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
package ch.hslu.swda.micro;

/**
 * Holds all constants for message routes.
 */
public class MessageRoutes {

    static final String ORDER_GET_ENTITY = "order.entity";
    static final String ORDER_GET_ENTITYSET = "order.entityset";
    static final String ORDER_CREATE = "order.create";
    static final String ORDER_STATUS = "order.status";
    static final String ORDER_UPDATE = "order.update";

    static final String CUSTOMER_GET_ENTITY = "customer.entity";
    static final String CUSTOMER_GET_ENTITYSET = "customer.entityset";

    static final String INVENTORY_GET_ENTITYSET = "inventory.entityset";
    static final String INVENTORY_PATCH = "inventory.patch";

    /**
     * No instance allowed.
     */
    private MessageRoutes() {
    }

}
