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
import ch.hslu.swda.entities.ReplenishmentOrder;
import ch.hslu.swda.entities.ReplenishmentOrderResponse;
import ch.hslu.swda.entities.ReplenishmentStatus;
import ch.hslu.swda.stock.local.StockLocal;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CreateReplenishmentOrderReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CreateReplenishmentOrderReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final Replenisher replenisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public CreateReplenishmentOrderReceiver(final String exchangeName, final BusConnector bus, Replenisher replenisher) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.replenisher = replenisher;
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {

        // receive message and reply
        try {
            LOG.debug("received chat message with replyTo property [{}]: [{}]", replyTo, message);

            ReplenishmentOrder request = mapper.readValue(message, ReplenishmentOrder.class);

            ReplenishmentOrderResponse response = replenisher.replenish(request.getProductId(), request.getCount());
            String data = (response != null) ? mapper.writeValueAsString(response) : "";

            LOG.debug("sending answer [{}] with topic [{}] according to replyTo-property", data, replyTo);
            bus.reply(exchangeName, replyTo, corrId, data);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
