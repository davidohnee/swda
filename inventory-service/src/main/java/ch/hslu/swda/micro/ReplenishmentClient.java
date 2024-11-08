package ch.hslu.swda.micro;

import ch.hslu.swda.entities.ReplenishmentOrder;
import ch.hslu.swda.entities.OrderInfo;

import java.io.IOException;

public interface ReplenishmentClient {
    OrderInfo replenish(ReplenishmentOrder order) throws IOException, InterruptedException;
}
