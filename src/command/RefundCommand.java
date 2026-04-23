package command;

/**
 * Design Pattern: Command (Concrete Command)
 * Encapsulates refund transaction operation.
 */

import memento.InventoryManager;

public class RefundCommand implements TransactionCommand {
    private InventoryManager inventoryManager;
    private String productId;
    private int amount;

    public RefundCommand(InventoryManager inventoryManager, String productId, int amount) {
        this.inventoryManager = inventoryManager;
        this.productId = productId;
        this.amount = amount;
    }

    @Override
    public void execute() {
        System.out.println("  [RefundCommand] Processing refund for " + amount + "x " + productId);
        inventoryManager.addItemStock(productId, amount);
        System.out.println("  [RefundCommand] Refund completed. Stock restored.");
    }

    @Override
    public void undo() {
        System.out.println("  [RefundCommand] Undoing refund...");
        inventoryManager.reduceStock(productId, amount);
    }
}
