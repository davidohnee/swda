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
import ch.hslu.swda.micro.receivers.CreateReplenishmentOrderReceiver;
import ch.hslu.swda.micro.receivers.GetReplenishmentOrderReceiver;
import ch.hslu.swda.micro.senders.InventoryItemGetter;
import ch.hslu.swda.micro.senders.OnItemReplenishedSender;
import ch.hslu.swda.stock.api.StockFactory;
import ch.hslu.swda.common.routing.MessageRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * Replenishment Service
 */
public final class ReplenishmentService implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(ReplenishmentService.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final Replenisher replenisher;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * @throws IOException      IO-Fehler.
     * @throws TimeoutException Timeout.
     */
    ReplenishmentService() throws IOException, TimeoutException {
        // thread info
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        // setup rabbitmq connection
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();

        this.replenisher = new Replenisher(
                StockFactory.getStock(),
                new InventoryItemGetter(this.exchangeName, this.bus),
                new OnItemReplenishedSender(this.exchangeName, this.bus));

        // start message receivers
        this.receiveCreateReplenishmentOrderMessages();
        this.receiveGetAllReplenishmentOrderMessages();
    }

    /**
     * @throws IOException
     */
    private void receiveCreateReplenishmentOrderMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", MessageRoutes.REPLENISHMENT_CREATE);
        bus.listenFor(exchangeName, "ReplenishmentService <- " + MessageRoutes.REPLENISHMENT_CREATE, MessageRoutes.REPLENISHMENT_CREATE, new CreateReplenishmentOrderReceiver(exchangeName, bus, replenisher));
    }

    /**
     * @throws IOException
     */
    private void receiveGetAllReplenishmentOrderMessages() throws IOException {
        LOG.debug("Starting listening for messages with routing [{}]", MessageRoutes.REPLENISHMENT_GET_ENTITYSET);
        bus.listenFor(
            exchangeName,
            "ReplenishmentService <- " + MessageRoutes.REPLENISHMENT_GET_ENTITYSET,
            MessageRoutes.REPLENISHMENT_GET_ENTITYSET,
            new GetReplenishmentOrderReceiver(exchangeName, bus, replenisher));
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() {
        bus.close();
    }
}
