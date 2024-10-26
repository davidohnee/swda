package ch.hslu.swda.entities;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order {

    private final long id;
    private final Date timestamp;
    private OrderStatus status;
    private final List<OrderItem> items;

    public Order(long id, Date timestamp, OrderStatus status, List<OrderItem> items) {
        this.id = id;
        this.timestamp = timestamp;
        this.status = status;
        this.items = items;
    }

    public long getId() {
        return this.id;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return this.items;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof Order order)
                &&
                (order.id == this.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order [" +
                "id='" + this.id + '\'' +
                ", timestamp='" + this.timestamp + '\'' +
                ", status='" + this.status + '\'' +
                ", items='" + this.items + '\'' +
                ']';
    }
}
