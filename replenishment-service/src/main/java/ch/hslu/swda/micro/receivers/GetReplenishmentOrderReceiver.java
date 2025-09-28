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
import ch.hslu.swda.common.entities.InventoryItem;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.entities.FullReplenishmentOrder;
import ch.hslu.swda.micro.Replenisher;
import ch.hslu.swda.models.ReplenishTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * Receiver for creating replenishment orders.
 *
 * Expects: null
 * Returns: FullReplenishmentOrder[]
 */
public final class GetReplenishmentOrderReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(GetReplenishmentOrderReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final Replenisher replenisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetReplenishmentOrderReceiver(final String exchangeName, final BusConnector bus, Replenisher replenisher) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.replenisher = replenisher;

        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {

        // receive message and reply
        try {
            LOG.debug("received chat message with replyTo property [{}]: [{}]", replyTo, message);

            String data = switch (route) {
                case MessageRoutes.REPLENISHMENT_GET_ENTITY -> {
                    String cleanedMessage = message.trim().replaceAll("^\"|\"$", "");
                    UUID trackingId = UUID.fromString(cleanedMessage);
                    ReplenishTask order = replenisher.getTask(trackingId);
                    yield (order != null) ? mapper.writeValueAsString(order) : "Replenishment not found";
                }
                case MessageRoutes.REPLENISHMENT_GET_ENTITYSET -> {
                    var tasks = replenisher.getTasks();
                    var orders = FullReplenishmentOrder.fromReplenishTasks(tasks);
                    yield mapper.writeValueAsString(orders);
                }
                default -> {
                    LOG.warn("Unknown route: {}", route);
                    yield "Unknown route";
                }
            };

            LOG.debug("sending answer [{}] with topic [{}] according to replyTo-property", data, replyTo);

            bus.reply(exchangeName, replyTo, corrId, data);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
