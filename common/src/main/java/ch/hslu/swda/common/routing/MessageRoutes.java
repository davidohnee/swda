package ch.hslu.swda.common.routing;

public final class MessageRoutes {

    public static final String ORDER_GET_ENTITY = "order.entity";
    public static final String ORDER_GET_ENTITYSET = "order.entityset";
    public static final String ORDER_CREATE = "order.create";
    public static final String ORDER_STATUS = "order.status";
    public static final String ORDER_UPDATE_ITEMS = "order.update.items";
    public static final String ORDER_UPDATE_STATUS = "order.update.status";

    public static final String CUSTOMER_GET_ENTITY = "customer.entity";
    public static final String CUSTOMER_GET_ENTITYSET = "customer.entityset";
    public static final String CUSTOMER_VALIDATE = "customer.validate";
    public static final String CUSTOMER_CREATE = "customer.create";
    public static final String CUSTOMER_UPDATE = "customer.update";

    public static final String INVENTORY_GET_ENTITY = "inventory.entity";
    public static final String INVENTORY_GET_ENTITYSET = "inventory.entityset";
    public static final String INVENTORY_PATCH = "inventory.patch";
    public static final String INVENTORY_TAKE = "inventory.take";
    public static final String INVENTORY_CANCEL = "inventory.cancel";
    public static final String INVENTORY_ON_AVAILABLE = "inventory.on.available";

    public static final String REPLENISHMENT_CREATE = "replenishment.create";
    public static final String REPLENISHMENT_GET_ENTITY = "replenishment.entity";
    public static final String REPLENISHMENT_GET_ENTITYSET = "replenishment.entityset";
    public static final String REPLENISHMENT_PATCH = "replenishment.patch";
    public static final String REPLENISHMENT_CANCEL = "replenishment.cancel";
    public static final String REPLENISHMENT_ON_REPLENISH = "replenishment.on.replenish";

    public static final String SHIPMENT_CREATE = "shipment.create";
    public static final String SHIPMENT_GET_ENTITY = "shipment.entity";
    public static final String SHIPMENT_GET_ENTITYSET = "shipment.entityset";
    public static final String SHIPMENT_VALIDATE = "shipment.validate";

    public static final String NOTIFICATION_GET_ENTITY = "notification.entity";
    public static final String NOTIFICATION_GET_ENTITYSET = "notification.entityset";
    public static final String NOTIFICATION_SEND = "notification.send";

    public static final String LOGGER_GET_ENTITY = "logger.entity";
    public static final String LOGGER_GET_ENTITYSET = "logger.entityset";
    public static final String LOGGER_PERSIST_LOG = "logger.log";
    /**
     * No instance allowed.
     */
    private MessageRoutes() {}
}
