package ch.hslu.swda.micro;


import ch.hslu.swda.common.entities.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

public class OrdersMemory {
    private final Map<UUID, Order> orders;

    public OrdersMemory() {
        this.orders = new HashMap<>();
        initializeOrders();
    }

    private void initializeOrders() {

        Order order1 = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                Order.StatusEnum.PENDING,
                Collections.emptyList(),
                new BigDecimal(100),
                Order.OrderTypeEnum.CUSTOMER_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "John",
                        "Doe",
                        new Address("Main Street", "1", "1234", "Springfield"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Jane",
                        "Doe",
                        Employee.RoleEnum.SALES
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.LOCAL
                )
        );

        Order order2 = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                Order.StatusEnum.PENDING,
                Collections.emptyList(),
                new BigDecimal(200),
                Order.OrderTypeEnum.CUSTOMER_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "Alice",
                        "Smith",
                        new Address("Second Street", "2", "5678", "Shelbyville"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Bob",
                        "Brown",
                        Employee.RoleEnum.MANAGER
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.LOCAL
                )
        );

        Order order3 = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now().minusDays(2),
                Order.StatusEnum.CONFIRMED,
                Collections.emptyList(),
                new BigDecimal(150),
                Order.OrderTypeEnum.REPLENISHMENT_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "Charlie",
                        "Johnson",
                        new Address("Third Street", "3", "9012", "Oaktown"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Diana",
                        "White",
                        Employee.RoleEnum.SALES
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.LOCAL
                )
        );

        Order order4 = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now().minusDays(5),
                Order.StatusEnum.PENDING,
                Collections.emptyList(),
                new BigDecimal(300),
                Order.OrderTypeEnum.CUSTOMER_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "Eve",
                        "Brown",
                        new Address("Fourth Avenue", "4A", "3456", "Mapleton"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Frank",
                        "Black",
                        Employee.RoleEnum.MANAGER
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.LOCAL
                )
        );

        Order order5 = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now().minusDays(1),
                Order.StatusEnum.PENDING,
                Collections.emptyList(),
                new BigDecimal(500),
                Order.OrderTypeEnum.CUSTOMER_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "Grace",
                        "Davis",
                        new Address("Fifth Road", "5B", "7890", "Riverdale"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Hank",
                        "Green",
                        Employee.RoleEnum.MANAGER
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.CENTRAL
                )
        );

        Order order6 = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now().minusDays(3),
                Order.StatusEnum.PENDING,
                Collections.emptyList(),
                new BigDecimal(750),
                Order.OrderTypeEnum.CUSTOMER_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "Ivy",
                        "Miller",
                        new Address("Sixth Lane", "6C", "6543", "Hill Valley"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Jack",
                        "Blue",
                        Employee.RoleEnum.SALES
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.LOCAL
                )
        );

        Order order7 = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now().minusDays(7),
                Order.StatusEnum.PENDING,
                Collections.emptyList(),
                new BigDecimal(1200),
                Order.OrderTypeEnum.CUSTOMER_ORDER,
                new Customer(
                        UUID.randomUUID(),
                        "Kelly",
                        "Clark",
                        new Address("Seventh Street", "7D", "8901", "Pinewood"),
                        new ContactInfo()
                ),
                new Employee(
                        UUID.randomUUID(),
                        "Leo",
                        "Red",
                        Employee.RoleEnum.MANAGER
                ),
                new Warehouse(
                        UUID.randomUUID(),
                        Warehouse.TypeEnum.LOCAL
                )
        );

        orders.put(order1.getId(), order1);
        orders.put(order2.getId(), order2);
        orders.put(order3.getId(), order3);
        orders.put(order4.getId(), order4);
        orders.put(order5.getId(), order5);
        orders.put(order6.getId(), order6);
        orders.put(order7.getId(), order7);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public Order getOrderById(UUID id) {
        return orders.get(id);
    }

    public void addOrder(Order order) {
        orders.put(order.getId(), order);
    }

    public void removeOrder(UUID id) {
        orders.remove(id);
    }
}
