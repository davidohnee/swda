package ch.hslu.swda.entities;

import java.util.Date;
import java.util.List;

public final class OrderBuilder {
    private long id;
    private Date timestamp;
    private OrderStatus status;
    private List<OrderItem> items;

    public OrderBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public OrderBuilder setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public OrderBuilder setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder setItems(List<OrderItem> items) {
        this.items = items;
        return this;
    }

    public Order build() {
        return new Order(id, timestamp, status, items);
    }
}
