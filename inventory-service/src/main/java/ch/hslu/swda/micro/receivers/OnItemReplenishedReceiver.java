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
package ch.hslu.swda.micro.receivers;

import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.inventory.Inventory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Receiver for getting items from the inventory.
 *
 * ENTITY_SET:
 * Expects: null
 * Returns: InventoryItem[]
 *
 * ENTITY:
 * Expects: int (productId)
 * Returns: InventoryItem
 */
public final class OnItemReplenishedReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(OnItemReplenishedReceiver.class);
    private final Inventory inventory;

    public OnItemReplenishedReceiver(Inventory inventory) {
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

            OrderInfo item = mapper.readValue(message, OrderInfo.class);

            inventory.handleReplenishment(item);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
