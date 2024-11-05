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
import ch.hslu.swda.bus.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * Beispielcode für Implementation eines Servcies mit RabbitMQ.
 */
public final class InventoryService implements AutoCloseable, ReplenishmentClientService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final Inventory inventory = new Inventory(this);

    /**
     * @throws IOException      IO-Fehler.
     * @throws TimeoutException Timeout.
     */
    InventoryService() throws IOException, TimeoutException {

        // thread info
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        // setup rabbitmq connection
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();

        // start message receivers
        this.receiveGetInventoryMessages();
        this.receiveUpdateInventoryMessages();
    }

    private void receiveUpdateInventoryMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", MessageRoutes.INVENTORY_PATCH);
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + MessageRoutes.INVENTORY_PATCH,
                MessageRoutes.INVENTORY_PATCH,
                new UpdateInventoryReceiver(exchangeName, bus, this.inventory)
        );
    }

    private void receiveGetInventoryMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", MessageRoutes.INVENTORY_GET_ENTITYSET);
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + MessageRoutes.INVENTORY_GET_ENTITYSET,
                MessageRoutes.INVENTORY_GET_ENTITYSET,
                new GetInventoryReceiver(exchangeName, bus, this.inventory)
        );
    }

    public String replenish() throws IOException, InterruptedException {
        // create question
        final String question = "What is the answer to the Ultimate Question of Life, the Universe, and Everything?";

        // send question to deep thought
        LOG.debug("Sending synchronous message to broker with routing [{}]", MessageRoutes.REPLENISHMENT_CREATE);
        String response = bus.talkSync(exchangeName, MessageRoutes.REPLENISHMENT_CREATE, question);

        // receive answer
        if (response == null) {
            LOG.debug("Received no response. Timeout occurred. Will retry later");
            return null;
        }
        LOG.debug("Received response to question \"{}\": {}", question, response);
        return response;
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() {
        bus.close();
    }
}
