package ch.hslu.swda.micro;

public class MessageRoutes {

    static final String ORDER_GET_ENTITY = "order.entity";
    static final String ORDER_GET_ENTITYSET = "order.entityset";
    static final String ORDER_CREATE = "order.create";
    static final String ORDER_STATUS = "order.status";
    static final String ORDER_UPDATE = "order.update";

    static final String CUSTOMER_GET_ENTITY = "customer.entity";
    static final String CUSTOMER_GET_ENTITYSET = "customer.entityset";

    static final String INVENTORY_GET_ENTITYSET = "inventory.entityset";
    static final String INVENTORY_GET_ENTITY = "inventory.entity";
    static final String INVENTORY_PATCH = "inventory.patch";
    static final String INVENTORY_TAKE = "inventory.take";

    static final String REPLENISHMENT_CREATE = "replenishment.create";
    static final String REPLENISHMENT_GET_ENTITYSET = "replenishment.entityset";
    static final String REPLENISHMENT_GET_ENTITY = "replenishment.entity";
    static final String REPLENISHMENT_PATCH = "replenishment.patch";

    /**
     * No instance allowed.
     */
    private MessageRoutes() {}

}
