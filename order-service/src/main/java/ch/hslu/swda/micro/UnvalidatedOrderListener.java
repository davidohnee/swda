package ch.hslu.swda.micro;

import ch.hslu.swda.model.Order;

/**
 * Listener for unvalidated orders.
 */
public interface UnvalidatedOrderListener {

    /**
     * Called when an order is unvalidated.
     *
     * @param order The unvalidated order.
     */
    void onUnvalidatedOrder(Order order);
}
