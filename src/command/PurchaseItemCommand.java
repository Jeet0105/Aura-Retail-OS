package command;

/**
 * Design Pattern: Command (Concrete Command) + Memento integration
 * Encapsulates purchase operation with state save/restore for rollback.
 */

import memento.InventoryManager;
import memento.InventoryState;
import factory.components.Dispenser;

public class PurchaseItemCommand implements TransactionCommand {
    private InventoryManager inventoryManager;
    private Dispenser dispenser;
    private String productId;
    private int amount;
    private InventoryState savedState;
    private boolean simulateHardwareFailure;

    public PurchaseItemCommand(InventoryManager inventoryManager, Dispenser dispenser, String productId, int amount) {
        this.inventoryManager = inventoryManager;
        this.dispenser = dispenser;
        this.productId = productId;
        this.amount = amount;
        this.simulateHardwareFailure = false;
    }

    public void setSimulateHardwareFailure(boolean simulate) {
        this.simulateHardwareFailure = simulate;
    }

    @Override
    public void execute() {
        // Save state before modifying
        savedState = inventoryManager.saveState();
        
        System.out.println("  [Command] Attempting to purchase " + amount + " of " + productId);
        boolean success = inventoryManager.reduceStock(productId, amount);
        
        if (success) {
            System.out.println("  [Command] Inventory deducted. Instructing dispenser...");
            try {
                if (simulateHardwareFailure) {
                    throw new RuntimeException("Simulated Dispenser Motor Failure");
                }
                for (int i = 0; i < amount; i++) {
                    dispenser.dispenseProduct(productId);
                }
                System.out.println("  [Command] Purchase completed successfully.");
            } catch (Exception e) {
                System.out.println("  [Command] Exception during dispense! " + e.getMessage());
                undo(); // Atomic rollback using Memento
            }
        } else {
            System.out.println("  [Command] Insufficient stock. Transaction failed.");
        }
    }

    @Override
    public void undo() {
        if (savedState != null) {
            System.out.println("  [Command] Rolling back transaction...");
            inventoryManager.restoreState(savedState);
        }
    }
}
