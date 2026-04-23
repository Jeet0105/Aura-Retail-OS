package command;

/**
 * Design Pattern: Command (Concrete Command)
 * Encapsulates inventory restock operation.
 */

import memento.InventoryManager;
import memento.InventoryState;

public class RestockCommand implements TransactionCommand {
    private InventoryManager inventoryManager;
    private String productId;
    private int amount;
    private InventoryState previousState;

    public RestockCommand(InventoryManager inventoryManager, String productId, int amount) {
        this.inventoryManager = inventoryManager;
        this.productId = productId;
        this.amount = amount;
    }

    @Override
    public void execute() {
        // Save state before restocking in case of failure
        previousState = inventoryManager.saveState();
        System.out.println("  [RestockCommand] Restocking " + amount + "x " + productId);
        inventoryManager.addItemStock(productId, amount);
        System.out.println("  [RestockCommand] Restock completed.");
    }

    @Override
    public void undo() {
        if (previousState != null) {
            System.out.println("  [RestockCommand] Undoing restock...");
            inventoryManager.restoreState(previousState);
        }
    }
}
