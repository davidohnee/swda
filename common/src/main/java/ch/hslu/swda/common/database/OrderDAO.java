package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class OrderDAO extends GenericDAO<Document> {
    private static final Logger LOG = LoggerFactory.getLogger(OrderDAO.class);


    public OrderDAO(MongoDatabase database) {
        super(database, "orders", Document.class);
    }

    public Order findByUUID(UUID orderId) {
        Document result = collection.aggregate(Arrays.asList(
                Aggregates.match(Filters.eq("orderId", orderId)),
                Aggregates.lookup("customers", "customerId", "_id", "customerDetails"),
                Aggregates.unwind("$customerDetails"),
                Aggregates.lookup("inventory", "orderItems.productId", "product._id", "productDetails"),
                Aggregates.project(new Document()
                        .append("id", "$orderId")
                        .append("dateTime", "$dateTime")
                        .append("status", "$status")
                        .append("orderItems", new Document("$map", new Document()
                                .append("input", "$orderItems")
                                .append("as", "item")
                                .append("in", new Document()
                                        .append("product", new Document()
                                                .append("id", "$$item.productId")
                                                .append("name", new Document("$arrayElemAt", Arrays.asList(
                                                        new Document("$filter", new Document()
                                                                .append("input", "$productDetails")
                                                                .append("as", "prod")
                                                                .append("cond", new Document("$eq", Arrays.asList("$$prod.product._id", "$$item.productId")))
                                                        ), 0
                                                )))
                                                .append("price", new Document("$arrayElemAt", Arrays.asList(
                                                        new Document("$filter", new Document()
                                                                .append("input", "$productDetails")
                                                                .append("as", "prod")
                                                                .append("cond", new Document("$eq", Arrays.asList("$$prod.product._id", "$$item.productId")))
                                                        ), 0
                                                )))
                                        )
                                        .append("quantity", "$$item.quantity")
                                )
                        ))
                        .append("price", "$price")
                        .append("orderType", "$orderType")
                        .append("customer", new Document()
                                .append("id", "$customerId")
                                .append("firstName", "$customerDetails.firstName")
                                .append("familyName", "$customerDetails.familyName")
                                .append("address", "$customerDetails.address")
                                .append("contactInfo", new Document("$ifNull", Arrays.asList("$customerDetails.contactInfo", new Document())))
                        )
                        .append("seller", new Document()
                                .append("id", "$employeeId")
                                .append("firstName", null)
                                .append("familyName", null)
                                .append("role", "SALES")
                        )
                        .append("destination", new Document()
                                .append("id", "$warehouseId")
                                .append("type", "LOCAL")
                        )
                )
        )).first();
        LOG.info("Found order: {}", result);

        if (result == null) {
            return null;
        }

        return convertDocumentToOrder(result);
    }


//    public List<Order> findAll() {
//        return
//    }


    private Order convertDocumentToOrder(Document doc) {
        Order order = new Order();
        order.setId(doc.get("id", UUID.class));
        order.setDateTime(OffsetDateTime.parse(doc.getString("dateTime")));
        System.out.println("DateTime of order: " + order.getDateTime());
        order.setStatus(Order.StatusEnum.valueOf(doc.getString("status")));
        order.setOrderItems(convertOrderItems(doc.getList("orderItems", Document.class)));
        order.setPrice(doc.get("price", BigDecimal.class));
        order.setOrderType(Order.OrderTypeEnum.valueOf(doc.getString("orderType")));
        order.setCustomer(convertCustomer(doc.get("customer", Document.class)));
        order.setSeller(convertSeller(doc.get("seller", Document.class)));
        order.setDestination(convertWarehouse(doc.get("destination", Document.class)));
        return order;
    }

    private List<OrderItem> convertOrderItems(List<Document> items) {
        return items.stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            Document productDoc = item.get("product", Document.class);
            Product product = new Product();
            product.setId(productDoc.getInteger("id"));
            Document nameDoc = productDoc.get("name", Document.class);
            product.setName(nameDoc.getString("name"));
            Document priceDoc = productDoc.get("price", Document.class);
            product.setPrice(priceDoc.get("price", BigDecimal.class));
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getInteger("quantity"));
            return orderItem;
        }).collect(Collectors.toList());
    }

    private Customer convertCustomer(Document customerDoc) {
        Customer customer = new Customer();
        customer.setId(customerDoc.get("id", UUID.class));
        customer.setFirstName(customerDoc.getString("firstName"));
        customer.setFamilyName(customerDoc.getString("familyName"));
        customer.setAddress(convertAddress(customerDoc.get("address", Document.class)));
        customer.setContactInfo(convertContactInfo(customerDoc.get("contactInfo", Document.class)));
        return customer;
    }

    private Employee convertSeller(Document sellerDoc) {
        Employee seller = new Employee();
        seller.setId(sellerDoc.get("id", UUID.class));
        seller.setFirstName(sellerDoc.getString("firstName"));
        seller.setFamilyName(sellerDoc.getString("familyName"));
        seller.setRole(Employee.RoleEnum.valueOf(sellerDoc.getString("role")));
        return seller;
    }

    private Warehouse convertWarehouse(Document warehouseDoc) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseDoc.get("id", UUID.class));
        warehouse.setType(Warehouse.TypeEnum.valueOf(warehouseDoc.getString("type")));
        return warehouse;
    }

    private Address convertAddress(Document addressDoc) {
        Address address = new Address();
        address.setStreetName(addressDoc.getString("streetName"));
        address.setStreetNumber(addressDoc.getString("streetNumber"));
        address.setPlz(addressDoc.getString("plz"));
        address.setTownName(addressDoc.getString("townName"));
        return address;
    }

    private ContactInfo convertContactInfo(Document contactInfoDoc) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(contactInfoDoc.getString("email"));
        contactInfo.setPhone(contactInfoDoc.getString("phone"));
        return contactInfo;
    }
}
