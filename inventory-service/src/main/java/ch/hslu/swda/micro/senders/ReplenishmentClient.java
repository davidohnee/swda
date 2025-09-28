package ch.hslu.swda.micro.senders;

import ch.hslu.swda.entities.ReplenishResponseHandler;
import ch.hslu.swda.dto.replenishment.ReplenishmentOrder;

import java.io.IOException;

public interface ReplenishmentClient {
    void replenish(ReplenishmentOrder order, ReplenishResponseHandler handler) throws IOException, InterruptedException;
}
