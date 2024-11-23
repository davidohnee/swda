package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
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
        Document result = collection.aggregate(getAggregation(orderId)).first();
        if (result == null) {
            return null;
        }
        return convertDocumentToOrder(result);
    }

    public List<Order> findAll() {
        List<Document> results = collection.aggregate(getAggregation(null)).into(new ArrayList<>());
        return results.stream().map(this::convertDocumentToOrder).toList();
    }

    private List<Bson> getAggregation(UUID orderId) {
        List<Bson> aggregation = new ArrayList<>();
        if (orderId != null) {
            aggregation.add(Aggregates.match(Filters.eq("orderId", orderId)));
        }
        aggregation.addAll(Arrays.asList(
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
                                                .append("name", new Document("$let", new Document()
                                                        .append("vars", new Document("productDetail", new Document("$arrayElemAt", Arrays.asList(
                                                                new Document("$filter", new Document()
                                                                        .append("input", "$productDetails")
                                                                        .append("as", "pd")
                                                                        .append("cond", new Document("$eq", Arrays.asList("$$pd.product._id", "$$item.productId")))
                                                                ), 0
                                                        ))))
                                                        .append("in", "$$productDetail.product.name")
                                                ))
                                                .append("price", new Document("$let", new Document()
                                                        .append("vars", new Document("productDetail", new Document("$arrayElemAt", Arrays.asList(
                                                                new Document("$filter", new Document()
                                                                        .append("input", "$productDetails")
                                                                        .append("as", "pd")
                                                                        .append("cond", new Document("$eq", Arrays.asList("$$pd.product._id", "$$item.productId")))
                                                                ), 0
                                                        ))))
                                                        .append("in", "$$productDetail.product.price")
                                                ))
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
        ));
        return aggregation;
    }


    private Order convertDocumentToOrder(Document doc) {
        Order order = new Order();
        order.setId(doc.get("id", UUID.class));
        order.setDateTime(OffsetDateTime.parse(doc.getString("dateTime")));
        System.out.println("DateTime of order: " + order.getDateTime());
        order.setStatus(Order.StatusEnum.valueOf(doc.getString("status")));
        order.setOrderItems(convertOrderItems(doc.getList("orderItems", Document.class)));
        order.setPrice(doc.get("price", Decimal128.class).bigDecimalValue());
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
            if (productDoc != null) {
                Product product = new Product();
                product.setId(productDoc.getInteger("id"));
                product.setName(productDoc.getString("name"));
                Object priceObj = productDoc.get("price");
                if (priceObj instanceof Decimal128) {
                    product.setPrice(((Decimal128) priceObj).bigDecimalValue());
                }
                orderItem.setProduct(product);
            }
            orderItem.setQuantity(item.getInteger("quantity", 0)); // Default to 0 if null
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
