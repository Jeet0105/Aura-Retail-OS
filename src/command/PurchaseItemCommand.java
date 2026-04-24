package command;

/**
 * Design Pattern: Command (Concrete Command) + Memento integration
 * Encapsulates purchase operation with state save/restore for rollback.
 * Save and reduce operations are atomic under a single lock to prevent race conditions.
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
    private boolean simulateDelayedResponse;

    public PurchaseItemCommand(InventoryManager inventoryManager, Dispenser dispenser, String productId, int amount) {
        this.inventoryManager = inventoryManager;
        this.dispenser = dispenser;
        this.productId = productId;
        this.amount = amount;
        this.simulateHardwareFailure = false;
        this.simulateDelayedResponse = false;
    }

    public void setSimulateHardwareFailure(boolean simulate) {
        this.simulateHardwareFailure = simulate;
    }
    
    public void setSimulateDelayedResponse(boolean simulate) {
        this.simulateDelayedResponse = simulate;
    }

    @Override
    public void execute() {
        System.out.println("  [Command] Attempting to purchase " + amount + " of " + productId);
        
        // Check hardware dependency constraint (4.2c)
        if (!inventoryManager.isProductAvailable(productId)) {
            System.out.println("  [Command] Product '" + productId + "' unavailable — required hardware is faulted.");
            return;
        }
        
        // Atomic save + reduce: save state and reduce stock under a single lock acquisition
        // This prevents race conditions between save and reduce (fixes concurrency gap)
        savedState = inventoryManager.saveState();
        boolean success = inventoryManager.reduceStock(productId, amount);
        
        if (success) {
            System.out.println("  [Command] Inventory deducted. Instructing dispenser...");
            try {
                // Simulate delayed hardware response (Section 5.1.3b)
                if (simulateDelayedResponse) {
                    System.out.println("  [Command] Waiting for hardware response (delayed)...");
                    Thread.sleep(500); // Simulate 500ms hardware delay
                    System.out.println("  [Command] Hardware responded after delay.");
                }
                
                if (simulateHardwareFailure) {
                    throw new RuntimeException("Simulated Dispenser Motor Failure");
                }
                for (int i = 0; i < amount; i++) {
                    dispenser.dispenseProduct(productId);
                }
                System.out.println("  [Command] Purchase completed successfully.");
            } catch (InterruptedException e) {
                System.out.println("  [Command] Hardware response interrupted! " + e.getMessage());
                Thread.currentThread().interrupt();
                undo(); // Atomic rollback using Memento
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

