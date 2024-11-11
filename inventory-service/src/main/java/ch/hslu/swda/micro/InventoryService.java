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
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.inventory.InMemoryInventory;
import ch.hslu.swda.inventory.Inventory;
import ch.hslu.swda.micro.receivers.GetInventoryReceiver;
import ch.hslu.swda.micro.receivers.OnItemReplenishedReceiver;
import ch.hslu.swda.micro.receivers.TakeFromInventoryReceiver;
import ch.hslu.swda.micro.receivers.UpdateInventoryReceiver;
import ch.hslu.swda.micro.senders.OnItemAvailableSender;
import ch.hslu.swda.micro.senders.ReplenishmentClientSender;
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
    private final Inventory inventory;

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

        this.inventory = new InMemoryInventory(
                new ReplenishmentClientSender(this.exchangeName, this.bus),
                new OnItemAvailableSender(this.exchangeName, this.bus));

        // start message receivers
        this.receiveGetInventoryMessages();
        this.receiveUpdateInventoryMessages();
        this.receiveTakeFromInventoryMessages();
        this.receiveOnItemReplenishedMessages();
    }

    private void receiveTakeFromInventoryMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", MessageRoutes.INVENTORY_TAKE);
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + MessageRoutes.INVENTORY_TAKE,
                MessageRoutes.INVENTORY_TAKE,
                new TakeFromInventoryReceiver(exchangeName, bus, this.inventory)
        );
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
        LOG.debug("Starting listening for messages with routing [{}]", MessageRoutes.INVENTORY_GET_ENTITY);
        MessageReceiver receiver = new GetInventoryReceiver(exchangeName, bus, this.inventory);
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + MessageRoutes.INVENTORY_GET_ENTITYSET,
                MessageRoutes.INVENTORY_GET_ENTITYSET,
                receiver
        );
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + MessageRoutes.INVENTORY_GET_ENTITY,
                MessageRoutes.INVENTORY_GET_ENTITY,
                receiver
        );
    }

    private void receiveOnItemReplenishedMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", MessageRoutes.REPLENISHMENT_ON_REPLENISH);
        bus.listenFor(
                exchangeName,
                "InventoryService <- " + MessageRoutes.REPLENISHMENT_ON_REPLENISH,
                MessageRoutes.REPLENISHMENT_ON_REPLENISH,
                new OnItemReplenishedReceiver(this.inventory)
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
