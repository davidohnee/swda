package ch.hslu.swda.common.routing;

public final class MessageRoutes {

    public static final String ORDER_GET_ENTITY = "order.entity";
    public static final String ORDER_GET_ENTITYSET = "order.entityset";
    public static final String ORDER_CREATE = "order.create";
    public static final String ORDER_STATUS = "order.status";
    public static final String ORDER_UPDATE = "order.update";

    public static final String CUSTOMER_GET_ENTITY = "customer.entity";
    public static final String CUSTOMER_GET_ENTITYSET = "customer.entityset";
    public static final String CUSTOMER_CREATE = "customer.create";

    public static final String INVENTORY_GET_ENTITYSET = "inventory.entityset";
    public static final String INVENTORY_PATCH = "inventory.patch";

    public static final String REPLENISHMENT_CREATE = "replenishment.create";
    public static final String REPLENISHMENT_GET_ENTITYSET = "replenishment.entityset";
    public static final String REPLENISHMENT_GET_ENTITY = "replenishment.entity";
    public static final String REPLENISHMENT_PATCH = "replenishment.patch";

    /**
     * No instance allowed.
     */
    private MessageRoutes() {}
}
