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
import ch.hslu.swda.common.config.ApplicationConfig;
import ch.hslu.swda.common.database.MongoDBConnectionManager;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Applikationsstart.
 */
public final class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final int RETRY_DELAY_MS = 2000;
    private static final int MAX_RETRIES = 60; // 2 minutes of retries
    private static MongoDatabase database;

    /**
     * Privater Konstruktor.
     */
    private Application() {
    }

    private static boolean waitForRabbitMQ() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try (BusConnector connector = new BusConnector()) {
                connector.connect();
                LOG.info("RabbitMQ available on attempt {}/{}.", attempt, MAX_RETRIES);
                return true;
            } catch (IOException | TimeoutException e) {
                LOG.error("Failed to connect to RabbitMQ. Retrying in {}ms. Attempt {}/{}.", RETRY_DELAY_MS, attempt, MAX_RETRIES);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    LOG.error("Interrupted while waiting for RabbitMQ.", ex);
                    return false;
                }
            }
        }
        LOG.error("Failed to connect to RabbitMQ after {} attempts.", MAX_RETRIES);
        return false;
    }

    /**
     * main-Methode. Startet einen Timer für den HeartBeat.
     *
     * @param args not used.
     */
    public static void main(final String[] args) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        LOG.info("Service starting...");

        LOG.info("Creating database connection...");
        MongoDBConnectionManager connectionManager = MongoDBConnectionManager.getInstance(
                ApplicationConfig.getConnectionString(),
                ApplicationConfig.getDatabaseName()
        );
        database = connectionManager.getDatabase();

        if (!"OFF".equals(System.getenv("RABBIT"))) {
            if (waitForRabbitMQ()) {
                final Timer timer = new Timer();
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 2);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);

                timer.schedule(new HeartBeat(), 0, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
            } else {
                LOG.error("RabbitMQ not available, exiting application.");
            }
        } else {
            LOG.atWarn().log("RabbitMQ disabled for testing.");
        }
        LOG.atInfo().addArgument(System.currentTimeMillis() - startTime).log("Service started in {}ms.");
        Thread.sleep(60_000);
    }

    /**
     * TimerTask für periodische Ausführung.
     */
    private static final class HeartBeat extends TimerTask {

        private static final Logger LOG = LoggerFactory.getLogger(HeartBeat.class);

        private ReplenishmentService service;

        HeartBeat() {
            try {
                this.service = new ReplenishmentService(database);
            } catch (IOException | TimeoutException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        @Override
        public void run() {
            this.service.heartbeat();
        }
    }
}
