package ch.hslu.swda.micro;

import ch.hslu.swda.entities.ReplenishmentOrder;
import ch.hslu.swda.entities.ReplenishmentStatus;

import java.io.IOException;

public interface ReplenishmentClientService {
    ReplenishmentStatus replenish(ReplenishmentOrder order) throws IOException, InterruptedException;
}
