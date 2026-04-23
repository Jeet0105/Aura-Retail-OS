package facade;

/**
 * Design Pattern: Facade
 * Unified interface providing simplified kiosk operations.
 * Diagnostics derive status from actual system conditions (requirement 4.1c).
 */

import factory.KioskFactory;
import memento.InventoryManager;
import command.PurchaseItemCommand;
import command.RefundCommand;
import command.RestockCommand;
import factory.components.Dispenser;
import state.KioskContext;
import strategy.PricingContext;

public class KioskInterface {
    private final KioskFactory factory;
    private final InventoryManager inventoryManager;
    private final Dispenser dispenser;
    private final KioskContext stateContext;
    private final PricingContext pricingContext;
    
    // Hardware status tracking for diagnostics (requirement 4.1c, 4.2c)
    private boolean hardwareOperational = true;
    private boolean networkConnected = true;

    public KioskInterface(KioskFactory factory, InventoryManager inventoryManager, PricingContext pricingContext) {
        this.factory = factory;
        this.inventoryManager = inventoryManager;
        this.dispenser = factory.createDispenser();
        this.stateContext = new KioskContext();
        this.pricingContext = pricingContext;
    }
    
    public KioskContext getStateContext() { return stateContext; }

    public void restockItem(String productId, int amount) {
        RestockCommand command = new RestockCommand(inventoryManager, productId, amount);
        command.execute();
        System.out.println("[Facade] Restocked " + amount + " " + productId);
    }
    
    public void simulateFailingPurchase(String userId, String productId, int amount, double basePrice) {
        purchaseInternal(userId, productId, amount, basePrice, true);
    }

    public void purchaseItem(String userId, String productId, int amount, double basePrice) {
        purchaseInternal(userId, productId, amount, basePrice, false);
    }

    private void purchaseInternal(String userId, String productId, int amount, double basePrice, boolean simulateFailure) {
        System.out.println("\n--- Initializing User Request ---");
        // Behavioral State
        stateContext.purchase();
        
        // Check if kiosk is in a state that allows purchases
        if (!stateContext.canPurchase()) {
            System.out.println("[Facade] Purchase denied - kiosk not in active mode");
            return;
        }
        
        // Check hardware dependency constraint (requirement 4.2c)
        if (!hardwareOperational) {
            System.out.println("[Facade] Purchase denied - hardware malfunction detected");
            return;
        }
        
        // Behavioral Strategy
        double finalPrice = pricingContext.executePricing(basePrice * amount);
        System.out.println("[Facade] Calculated price for " + amount + "x " + productId + " = ₹" + finalPrice);
        
        // Product constraints validation using Abstract Factory components
        boolean verified = factory.createVerificationModule().verify(userId, productId);
        boolean inStock = factory.createInventoryPolicy().canPurchase(productId, amount, inventoryManager.getAvailableStock(productId));

        if (verified && inStock) {
            // Execute Behavioral Command
            PurchaseItemCommand command = new PurchaseItemCommand(inventoryManager, dispenser, productId, amount);
            command.setSimulateHardwareFailure(simulateFailure);
            command.execute();
        } else {
            System.out.println("[Facade] Policy or verification failed. Purchase declined.");
        }
    }

    public void refundTransaction(String userId, String productId, int amount) {
        System.out.println("\n--- Initiating Refund ---");
        System.out.println("[Facade] Processing refund for user " + userId);
        RefundCommand refundCommand = new RefundCommand(inventoryManager, productId, amount);
        refundCommand.execute();
        System.out.println("[Facade] Refund transaction completed.");
    }

    /**
     * Runs diagnostics deriving status from actual system conditions.
     * Addresses requirement 4.1c - operational status derived from hardware, mode, network.
     */
    public void runDiagnostics() {
        System.out.println("\n--- Running Kiosk Diagnostics ---");
        
        // Derive operational status from actual conditions
        String currentState = stateContext.getCurrentStateInfo();
        String hardwareStatus = hardwareOperational ? "Functional" : "MALFUNCTION";
        String networkStatus = networkConnected ? "Connected" : "DISCONNECTED";
        
        // Determine overall operational status
        boolean isActiveMode = currentState.contains("ActiveMode");
        boolean fullyOperational = hardwareOperational && networkConnected && isActiveMode;
        
        System.out.println("[Diagnostics] Current State: " + currentState);
        System.out.println("[Diagnostics] Hardware Status: " + hardwareStatus);
        System.out.println("[Diagnostics] Network Status: " + networkStatus);
        System.out.println("[Diagnostics] Inventory Manager Status: " + (inventoryManager != null ? "Operational" : "FAILED"));
        System.out.println("[Diagnostics] Dispenser Status: " + (dispenser != null ? hardwareStatus : "NOT INSTALLED"));
        System.out.println("[Diagnostics] Pricing Context: " + pricingContext.getCurrentStrategy());
        System.out.println("[Diagnostics] Overall Status: " + (fullyOperational ? "FULLY OPERATIONAL" : "DEGRADED/LIMITED"));
        System.out.println("[Diagnostics] Diagnostics Complete\n");
    }
    
    /**
     * Sets hardware operational status for testing hardware dependency constraint.
     */
    public void setHardwareStatus(boolean operational) {
        this.hardwareOperational = operational;
        if (!operational) {
            System.out.println("[Facade] Hardware fault detected - marking related products unavailable");
        }
    }
    
    /**
     * Sets network connection status for testing.
     */
    public void setNetworkStatus(boolean connected) {
        this.networkConnected = connected;
    }
    
    public boolean isHardwareOperational() {
        return hardwareOperational;
    }
    
    public boolean isNetworkConnected() {
        return networkConnected;
    }
}
