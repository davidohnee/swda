package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final int RETRY_DELAY_MS = 2000;
    private static final int MAX_RETRIES = 60; // 2 minutes of retries

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
        if (!"OFF".equals(System.getenv("RABBIT"))) {
            if (waitForRabbitMQ()) {
                var customerServiceThread = new Thread(new ServiceWrapper());
                customerServiceThread.start();
            } else {
                LOG.error("RabbitMQ not available, exiting application.");
            }
        } else {
            LOG.atWarn().log("RabbitMQ disabled for testing.");
        }
        LOG.atInfo().addArgument(System.currentTimeMillis() - startTime).log("Service started in {}ms.");
        Thread.sleep(60_000);
    }

    private static final class ServiceWrapper implements Runnable {
        private static  final  Logger LOG = LoggerFactory.getLogger(ServiceWrapper.class);
        ServiceWrapper() {}

        @Override
        public void run() {
            try {
                new NotificationService();
            } catch (IOException | TimeoutException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
