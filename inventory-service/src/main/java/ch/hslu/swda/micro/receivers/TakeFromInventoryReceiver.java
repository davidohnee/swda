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

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.common.entities.OrderInfo;
import ch.hslu.swda.dto.inventory.InventoryTakeRequest;
import ch.hslu.swda.dto.inventory.InventoryUpdateRequest;
import ch.hslu.swda.inventory.Inventory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Receiver for taking items from the inventory.
 *
 * Expects: InventoryTakeRequest
 * Returns: OrderInfo[]
 */
public final class TakeFromInventoryReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(TakeFromInventoryReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final ObjectMapper mapper;
    private final Inventory inventory;

    public TakeFromInventoryReceiver(final String exchangeName, final BusConnector bus, Inventory inventory) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.inventory = inventory;
        this.mapper = new ObjectMapper();
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {
        LOG.debug("Received message with routing [{}]", route);
        try {
            LOG.debug("Received message: {}", message);

            InventoryTakeRequest request = mapper.readValue(message, InventoryTakeRequest.class);
            OrderInfo[] orderInfo = new OrderInfo[request.getItems().size()];
            List<InventoryUpdateRequest> items = request.getItems();

            for (int i = 0; i < items.size(); i++) {
                var item = items.get(i);
                orderInfo[i] = this.inventory.take(item.getProductId(), item.getQuantity());
            }

            String data = mapper.writeValueAsString(orderInfo);

            LOG.debug("Sending response: {}", data);
            bus.reply(exchangeName, replyTo, corrId, data);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid UUID format: {}", message, e);
            sendErrorResponse(replyTo, corrId, "Invalid UUID format");
        } catch (IOException e) {
            LOG.error("Error processing message", e);
            sendErrorResponse(replyTo, corrId, "Error processing request");
        }
    }

    private void sendErrorResponse(String replyTo, String corrId, String errorMessage) {
        try {
            bus.reply(exchangeName, replyTo, corrId, errorMessage);
        } catch (IOException e) {
            LOG.error("Error sending error response", e);
        }
    }
}
