package ch.hslu.swda.micro;

import ch.hslu.swda.entities.ReplenishResponseHandler;
import ch.hslu.swda.entities.ReplenishmentOrder;
import ch.hslu.swda.entities.OrderInfo;

import java.io.IOException;

public interface ReplenishmentClient {
    void replenish(ReplenishmentOrder order, ReplenishResponseHandler handler) throws IOException, InterruptedException;
}
