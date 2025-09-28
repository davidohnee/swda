package ch.hslu.swda.entities;

import ch.hslu.swda.common.entities.OrderInfo;

public interface ReplenishResponseHandler {
    void handle(OrderInfo orderInfo);
}
