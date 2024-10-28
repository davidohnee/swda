package ch.hslu.swda.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static final class ServiceWrapper implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(ServiceWrapper.class);

        public ServiceWrapper() {
        }

        @Override
        public void run() {
            try {
                new CustomerService();
            } catch (IOException | TimeoutException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Private constructor
     */
    private Application() {
    }

    public static void main(String[] args) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        LOG.info("Customer service starting...");
        final ServiceWrapper serviceWrapper = new ServiceWrapper();
        Thread customerServiceThread = new Thread(serviceWrapper);
        customerServiceThread.start();

        LOG.atInfo().addArgument(System.currentTimeMillis() - startTime).log("Customer service started in {}ms.");
    }
}
