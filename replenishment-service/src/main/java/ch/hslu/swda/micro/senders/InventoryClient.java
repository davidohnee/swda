package ch.hslu.swda.micro.senders;

import java.io.IOException;

public interface InventoryClient {
    void getInventoryItem(int productId, InventoryResponseHandler handler) throws IOException, InterruptedException;
}
