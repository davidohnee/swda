package ch.hslu.swda.micro.order;


import ch.hslu.swda.common.entities.PersistedOrder;

/**
 * Listener for unvalidated orders.
 */
public interface UnvalidatedOrderListener {

    /**
     * Called when an order is unvalidated.
     *
     * @param order The unvalidated order.
     */
    void onUnvalidatedOrder(PersistedOrder order);
}
