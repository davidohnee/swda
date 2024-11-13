package ch.hslu.swda.micro.senders;

import ch.hslu.swda.common.entities.OrderInfo;

public interface OnItemAvailable {
    void onItemAvailable(OrderInfo orderInfo);
}
