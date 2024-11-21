package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Shipment;
import ch.hslu.swda.common.entities.Order;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class ShipmentDAO extends GenericDAO<Shipment> {
    private final OrderDAO orderDAO;

    public ShipmentDAO(MongoDatabase database) {
        super(database, "shipments", Shipment.class);
        this.orderDAO = new OrderDAO(database);
    }

    public Shipment findByUUID(UUID id) {
        Shipment shipment = collection.find(eq("id", id.toString())).first();
        if (shipment != null) {
            Order order = orderDAO.findByUUID(shipment.getOrderId());
            shipment.setCustomerOrder(order);
        }
        return shipment;
    }

    public List<Shipment> findAll() {
        List<Shipment> shipments = collection.find().into(new ArrayList<>());
        for (Shipment shipment : shipments) {
            Order order = orderDAO.findByUUID(shipment.getOrderId());
            shipment.setCustomerOrder(order);
        }
        return shipments;
    }

    @Override
    public void create(Shipment shipment) {
        shipment.setCustomerOrder(null);
        collection.insertOne(shipment);
    }
}
