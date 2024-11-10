package ch.hslu.swda.micro;

import java.io.IOException;

public interface InventoryClient {
    void getInventoryItem(int productId, InventoryResponseHandler handler) throws IOException, InterruptedException;
}
