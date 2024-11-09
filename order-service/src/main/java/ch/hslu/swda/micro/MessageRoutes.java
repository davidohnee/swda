package ch.hslu.swda.micro;

public final class MessageRoutes {

    public static final String ORDER_GET_ENTITY = "order.entity";
    public static final String ORDER_GET_ENTITYSET = "order.entityset";
    public static final String ORDER_CREATE = "order.create";
    public static final String ORDER_STATUS = "order.status";
    public static final String ORDER_UPDATE = "order.update";

    public static final String CUSTOMER_GET_ENTITY = "customer.entity";
    public static final String CUSTOMER_GET_ENTITYSET = "customer.entityset";
    public static final String CUSTOMER_VALIDATE = "customer.validate";

    public static final String INVENTORY_GET_ENTITYSET = "inventory.entityset";
    public static final String INVENTORY_PATCH = "inventory.patch";
    public static final String INVENTORY_TAKE = "inventory.take";

    /**
     * No instance allowed.
     */
    private MessageRoutes() {
    }

}
