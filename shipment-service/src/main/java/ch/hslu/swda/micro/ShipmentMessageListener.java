package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.common.database.ShipmentDAO;
import ch.hslu.swda.common.entities.Order;
import ch.hslu.swda.common.routing.MessageRoutes;
import ch.hslu.swda.micro.receivers.CreateShipmentReceiver;
import ch.hslu.swda.micro.receivers.GetShipmentReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ShipmentMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(ShipmentMessageListener.class);

    private final String exchangeName;
    private final BusConnector bus;
    private final ShipmentDAO shipmentDAO;

    public ShipmentMessageListener(String exchangeName, BusConnector bus, ShipmentDAO shipmentDAO) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.shipmentDAO = shipmentDAO;
    }

    public void start() throws IOException {
        LOG.info("ShipmentService is now listening for messages...");

        this.bus.listenFor(
            this.exchangeName,
            "ShipmentService <- shipment.get.entityset",
            MessageRoutes.SHIPMENT_GET_ENTITYSET,
            new GetShipmentReceiver(this.exchangeName, this.bus, this.shipmentDAO)
        );

        this.bus.listenFor(
            this.exchangeName,
            "ShipmentService <- shipment.get.entity",
            MessageRoutes.SHIPMENT_GET_ENTITY,
            new GetShipmentReceiver(this.exchangeName, this.bus, this.shipmentDAO)
        );

        this.bus.listenFor(
            this.exchangeName,
            "ShipmentService <- shipment.create",
            MessageRoutes.SHIPMENT_CREATE,
            new CreateShipmentReceiver(
                this.exchangeName,
                this.bus,
                this.shipmentDAO
            )
        );

    }
}
