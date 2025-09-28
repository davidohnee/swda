package ch.hslu.swda.micro.senders;

import ch.hslu.swda.dto.replenishment.ReplenishmentOrderResponse;

public interface OnItemReplenished {
    void onItemReplenished(ReplenishmentOrderResponse response);
}
