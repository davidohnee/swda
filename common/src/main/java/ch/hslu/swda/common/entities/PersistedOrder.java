package ch.hslu.swda.common.entities;

import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class PersistedOrder {
    private ObjectId id;
    private UUID orderId;
    private OffsetDateTime dateTime;
    private Order.StatusEnum status;
    private List<OrderItemCreate> orderItems;
    private BigDecimal price;
    private Order.OrderTypeEnum orderType;
    private UUID customerId;
    private UUID employeeId;
    private UUID warehouseId;

    public PersistedOrder(
            ObjectId id,
            UUID orderId,
            OffsetDateTime dateTime,
            Order.StatusEnum status,
            List<OrderItemCreate> orderItems,
            BigDecimal price,
            Order.OrderTypeEnum orderType,
            UUID customerId,
            UUID employeeId,
            UUID warehouseId
    ) {
        this.id = id;
        this.orderId = orderId;
        this.dateTime = dateTime;
        this.status = status;
        this.orderItems = orderItems;
        this.price = price;
        this.orderType = orderType;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.warehouseId = warehouseId;
    }

    public PersistedOrder() {
    }

    public ObjectId getId() {
        return this.id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return this.orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public OffsetDateTime getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Order.StatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(Order.StatusEnum status) {
        this.status = status;
    }

    public List<OrderItemCreate> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(List<OrderItemCreate> orderItems) {
        this.orderItems = orderItems;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Order.OrderTypeEnum getOrderType() {
        return this.orderType;
    }

    public void setOrderType(Order.OrderTypeEnum orderType) {
        this.orderType = orderType;
    }

    public UUID getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public UUID getWarehouseId() {
        return this.warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof PersistedOrder that)
                &&
                Objects.equals(that.id, this.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PersistedOrder{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", dateTime=" + dateTime +
                ", status=" + status +
                ", orderItems=" + orderItems +
                ", price=" + price +
                ", orderType=" + orderType +
                ", customerId=" + customerId +
                ", employeeId=" + employeeId +
                ", warehouseId=" + warehouseId +
                '}';
    }
}
