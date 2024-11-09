package ch.hslu.swda.micro.inventory;

public class InventoryTakeItemsException extends RuntimeException {
    public InventoryTakeItemsException(String message, Throwable cause) {
        super(message, cause);
    }
    public InventoryTakeItemsException(String message) {
        super(message);
    }
}
