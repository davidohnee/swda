package ch.hslu.swda.entities;

import java.util.Objects;

public final class CustomerOrder {
    private final Order order;
    private final Customer customer;
    private final Employee seller;

    public CustomerOrder(Order order, Customer customer, Employee seller) {
        this.order = order;
        this.customer = customer;
        this.seller = seller;
    }

    public Order getOrder() {
        return this.order;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Employee getSeller() {
        return this.seller;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof CustomerOrder that)
                &&
                Objects.equals(that.order, this.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order);
    }

    @Override
    public String toString() {
        return "CustomerOrder [" +
                "order='" + this.order + '\'' +
                ", customer='" + this.customer + '\'' +
                ", seller='" + this.seller + '\'' +
                ']';
    }
}
