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

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.entities.InventoryItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public final class GetInventoryReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(GetInventoryReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final Inventory inventory;

    public GetInventoryReceiver(
            final String exchangeName,
            final BusConnector bus,
            Inventory inventory
    ) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.inventory = inventory;
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {
        // receive message and reply
        try {
            LOG.debug("received get inventory message with replyTo property [{}]: [{}]", replyTo, message);
            LOG.debug("sending answer with topic [{}] according to replyTo-property", replyTo);

            ObjectMapper mapper = new ObjectMapper();

            String data = switch (route) {
                case MessageRoutes.INVENTORY_GET_ENTITY -> {
                    String cleanedMessage = message.trim().replaceAll("^\"|\"$", "");
                    int productId = Integer.parseInt(cleanedMessage);
                    InventoryItem order = this.inventory.get(productId);
                    yield (order != null) ? mapper.writeValueAsString(order) : "Product not found";
                }
                case MessageRoutes.INVENTORY_GET_ENTITYSET -> {
                    var orders = inventory.getAll();
                    yield mapper.writeValueAsString(orders);
                }
                default -> {
                    LOG.warn("Unknown route: {}", route);
                    yield "Unknown route";
                }
            };

            LOG.debug("sending response: {}", data);

            bus.reply(exchangeName, replyTo, corrId, data);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
