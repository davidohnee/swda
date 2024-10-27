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
public final class InventoryService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final Inventory inventory = new Inventory();

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
        this.receiveTakeFromInventoryMessages();
        this.receiveGetInventoryMessages();
        this.receiveUpdateInventoryMessages();
    }

    public void heartbeat() throws IOException {
        bus.talkAsync(exchangeName, InventoryRoutes.HEARTBEAT, "ping");
    }

    private void receiveUpdateInventoryMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", InventoryRoutes.UPDATE);
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + InventoryRoutes.UPDATE,
                InventoryRoutes.UPDATE,
                new UpdateInventoryReceiver(exchangeName, bus)
        );
    }

    private void receiveGetInventoryMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", InventoryRoutes.GET);
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + InventoryRoutes.GET,
                InventoryRoutes.GET,
                new GetInventoryReceiver(exchangeName, bus, this.inventory)
        );
    }

    private void receiveTakeFromInventoryMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", InventoryRoutes.TAKE_FROM);
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + InventoryRoutes.TAKE_FROM,
                InventoryRoutes.TAKE_FROM,
                new TakeFromInventoryReceiver(exchangeName, bus, this.inventory)
        );
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() {
        bus.close();
    }
}
