/**
 * Aura Retail OS - Main Simulation
 * 
 * Demonstrates all 9 design patterns:
 * Singleton, Abstract Factory, Command, Memento, State, Strategy,
 * Chain of Responsibility, Observer, and Facade.
 */
import core.CentralRegistry;
import core.EventBus;
import core.events.EmergencyModeActivated;
import core.events.HardwareFailureEvent;
import core.events.LowStockEvent;
import facade.KioskInterface;
import factory.EmergencyReliefKioskFactory;
import factory.FoodKioskFactory;
import factory.PharmacyKioskFactory;
import failure.FailureHandler;
import failure.RecalibrationHandler;
import failure.RetryHandler;
import failure.TechnicianAlertHandler;
import memento.InventoryManager;
import persistence.PersistenceManager;
import state.EmergencyLockdownMode;
import state.MaintenanceMode;
import state.PowerSavingMode;
import strategy.DiscountedPricing;
import strategy.EmergencyPricing;
import strategy.PricingContext;
import strategy.StandardPricing;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    
    // Helper method to wait for user input before proceeding
    private static void waitForUser(String scenarioName) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SCENARIO: " + scenarioName);
        System.out.println("=".repeat(60));
        System.out.print("\nPress ENTER to start this scenario...");
        scanner.nextLine();
        System.out.println();
    }
    
    public static void main(String[] args) {
        System.out.println("=== Aura Retail OS Starting ===");
        System.out.println("This simulation will run through multiple scenarios.");
        System.out.println("You will be prompted to press ENTER before each scenario begins.\n");
        
        // Initialize persistence layer
        PersistenceManager persistence = new PersistenceManager();
        
        // 1. Setup Singleton Registry
        CentralRegistry registry = CentralRegistry.getInstance();
        Map<String, Object> config = new HashMap<>();
        config.put("city", "Zephyrus");
        config.put("emergency_mode", false);
        persistence.loadConfiguration(config);
        registry.setConfig("city", "Zephyrus");
        registry.setConfig("emergency_mode", false);
        System.out.println("[Registry] City configured: " + registry.getConfig("city"));
        
        // 2. Setup Chain of Responsibility for Failure handling
        FailureHandler retry = new RetryHandler();
        FailureHandler recalibrate = new RecalibrationHandler();
        FailureHandler alert = new TechnicianAlertHandler();
        retry.setNextHandler(recalibrate);
        recalibrate.setNextHandler(alert);
        
        // 3. Setup Observer / EventBus for decoupled comms
        EventBus bus = EventBus.getInstance();
        
        // Register failure observer
        bus.subscribe("HARDWARE_FAILURE", event -> {
            HardwareFailureEvent failEvent = (HardwareFailureEvent) event;
            System.out.println("\n[CITY MONITORING] Received Alert: " + failEvent.getComponentName() + " failed.");
            System.out.println("[CITY MONITORING] Initiating failure recovery chain...");
            retry.handleFailure(failEvent.getErrorMessage());
        });

        // Register low stock observer
        bus.subscribe("LOW_STOCK", event -> {
            LowStockEvent lowEvent = (LowStockEvent) event;
            System.out.println("\n[SUPPLY CHAIN] Alert: Low stock on " + lowEvent.getProductId() + 
                             " (Current: " + lowEvent.getCurrentStock() + ", Threshold: " + lowEvent.getThreshold() + ")");
            System.out.println("[SUPPLY CHAIN] Ordering replenishment...");
        });
        
        // ========== PHARMACY KIOSK TEST ==========
        waitForUser("PHARMACY KIOSK DEPLOYMENT");
        InventoryManager pharmacyInventory = new InventoryManager();
        PricingContext pharmacyPricing = new PricingContext(new StandardPricing());
        KioskInterface pharmacyKiosk = new KioskInterface(new PharmacyKioskFactory(), pharmacyInventory, pharmacyPricing);
        
        System.out.println("\n-> Restocking items...");
        pharmacyKiosk.restockItem("Amoxicillin", 50);
        pharmacyKiosk.restockItem("Aspirin", 30);
        persistence.recordTransaction("SYSTEM", "Amoxicillin", 50, 0, "RESTOCK");

        System.out.println("\n-> Normal Purchase Scenario (State: ActiveMode, Strategy: StandardPricing)");
        pharmacyKiosk.purchaseItem("User123", "Amoxicillin", 2, 10.0);
        persistence.recordTransaction("User123", "Amoxicillin", 2, 20.0, "SUCCESS");

        System.out.println("\n-> Hardware Failure Scenario (Transaction Rollback + Chain of Responsibility)");
        System.out.println("Current Stock before failure: " + pharmacyInventory.getStock("Amoxicillin"));
        System.out.println("Available Stock before failure: " + pharmacyInventory.getAvailableStock("Amoxicillin"));
        pharmacyKiosk.simulateFailingPurchase("User456", "Amoxicillin", 5, 10.0);
        bus.publish(new HardwareFailureEvent("Pharmacy-Dispenser-Unit-1", "Motor stalled"));
        System.out.println("Final Stock after failed transaction: " + pharmacyInventory.getStock("Amoxicillin"));
        System.out.println("Available Stock after failed transaction: " + pharmacyInventory.getAvailableStock("Amoxicillin"));
        persistence.recordTransaction("User456", "Amoxicillin", 5, 50.0, "FAILED_ROLLBACK");
        
        System.out.println("\n-> Diagnostics Check");
        pharmacyKiosk.runDiagnostics();
        
        // Save inventory to demonstrate persistence (fixing requirement 3.2 gap)
        System.out.println("\n-> Saving inventory state to file...");
        persistence.saveInventory(pharmacyInventory.getSnapshot());

        // ========== FOOD KIOSK TEST ==========
        waitForUser("FOOD KIOSK DEPLOYMENT");
        InventoryManager foodInventory = new InventoryManager();
        PricingContext foodPricing = new PricingContext(new DiscountedPricing(10)); // 10% discount
        KioskInterface foodKiosk = new KioskInterface(new FoodKioskFactory(), foodInventory, foodPricing);
        
        System.out.println("\n-> Restocking Food Items...");
        foodKiosk.restockItem("CoffeeCup", 40);
        foodKiosk.restockItem("Sandwich", 25);
        persistence.recordTransaction("SYSTEM", "CoffeeCup", 40, 0, "RESTOCK");

        System.out.println("\n-> Purchase with Discounted Pricing (10% off)");
        foodKiosk.purchaseItem("User789", "CoffeeCup", 2, 5.0);
        persistence.recordTransaction("User789", "CoffeeCup", 2, 9.0, "SUCCESS");

        System.out.println("\n-> Low Stock Event Trigger");
        foodInventory.reduceStock("Sandwich", 24); // Leave 1 item
        bus.publish(new LowStockEvent("Sandwich", 1, 5));

        // ========== EMERGENCY RELIEF KIOSK TEST ==========
        waitForUser("EMERGENCY RELIEF KIOSK DEPLOYMENT");
        InventoryManager reliefInventory = new InventoryManager();
        PricingContext reliefPricing = new PricingContext(new StandardPricing());
        KioskInterface emergencyKiosk = new KioskInterface(new EmergencyReliefKioskFactory(), reliefInventory, reliefPricing);
        emergencyKiosk.restockItem("WaterBottle", 100);
        emergencyKiosk.restockItem("FirstAidKit", 50);
        persistence.recordTransaction("SYSTEM", "WaterBottle", 100, 0, "RESTOCK");

        // Emergency listener
        bus.subscribe("EMERGENCY_MODE", event -> {
            System.out.println("\n[SYSTEM] Emergency Mode Broadcasted city-wide.");
            // Change State and Strategy dynamically
            emergencyKiosk.getStateContext().setState(new EmergencyLockdownMode());
            reliefPricing.setStrategy(new EmergencyPricing());
            registry.setConfig("emergency_mode", true);
        });
        
        bus.publish(new EmergencyModeActivated("Earthquake detected"));
        
        System.out.println("\n-> Emergency Purchase Scenario (State: EmergencyLockdownMode, Strategy: EmergencyPricing, Policy: Max 2)");
        System.out.println("Attempting to buy 5 items (should be blocked)...");
        emergencyKiosk.purchaseItem("User999", "WaterBottle", 5, 5.0);
        
        System.out.println("\nAttempting to buy 2 items (valid under emergency rations)...");
        emergencyKiosk.purchaseItem("User999", "WaterBottle", 2, 5.0);
        persistence.recordTransaction("User999", "WaterBottle", 2, 5.0, "SUCCESS_EMERGENCY");

        // ========== STATE MACHINE FULL COVERAGE TEST ==========
        waitForUser("STATE MACHINE COVERAGE TEST");
        System.out.println("\n-> Testing all state transitions...");
        
        InventoryManager testInventory = new InventoryManager();
        testInventory.addItemStock("TestItem", 20);
        PricingContext testPricing = new PricingContext(new StandardPricing());
        KioskInterface testKiosk = new KioskInterface(new PharmacyKioskFactory(), testInventory, testPricing);
        
        System.out.println("[State] Current: ActiveMode");
        testKiosk.purchaseItem("StateTest1", "TestItem", 1, 5.0);
        
        System.out.println("\n[State] Transitioning to PowerSavingMode...");
        testKiosk.getStateContext().setState(new PowerSavingMode());
        testKiosk.purchaseItem("StateTest2", "TestItem", 1, 5.0);
        
        System.out.println("\n[State] Transitioning to MaintenanceMode...");
        testKiosk.getStateContext().setState(new MaintenanceMode());
        testKiosk.purchaseItem("StateTest3", "TestItem", 1, 5.0);
        
        System.out.println("\n[State] Transitioning back to ActiveMode for recovery...");
        testKiosk.getStateContext().setState(new MaintenanceMode());
        System.out.println("(Note: MaintenanceMode has no exit - would need separate controller for recovery)");

        // ========== REFUND SCENARIO TEST ==========
        waitForUser("REFUND PROCESSING TEST");
        System.out.println("Pharmacy Stock before refund: " + pharmacyInventory.getStock("Amoxicillin"));
        pharmacyKiosk.refundTransaction("User123", "Amoxicillin", 2);
        System.out.println("Pharmacy Stock after refund: " + pharmacyInventory.getStock("Amoxicillin"));
        persistence.recordTransaction("User123", "Amoxicillin", 2, 20.0, "REFUND");

        // ========== CONCURRENCY TEST ==========
        waitForUser("CONCURRENCY INTEGRITY TEST");
        System.out.println("Initial Amoxicillin stock: " + pharmacyInventory.getStock("Amoxicillin"));
        System.out.println("Spawning 2 concurrent purchase threads (10 items each)...");
        
        Thread t1 = new Thread(() -> pharmacyKiosk.purchaseItem("UserA", "Amoxicillin", 10, 10.0));
        Thread t2 = new Thread(() -> pharmacyKiosk.purchaseItem("UserB", "Amoxicillin", 10, 10.0));
        t1.start(); 
        t2.start();
        
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}
        
        int finalStock = pharmacyInventory.getStock("Amoxicillin");
        System.out.println("Final Amoxicillin stock: " + finalStock);
        
        // Verify atomicity: started with 50, bought 2 (48), failed 5 (48), refunded 2 (50), then 2 threads bought 10 each = 50-20 = 30
        int expectedStock = 30;
        if (finalStock == expectedStock) {
            System.out.println("[CONCURRENCY VERIFIED] ✓ Stock is atomic. Expected: " + expectedStock + ", Got: " + finalStock);
        } else {
            System.out.println("[CONCURRENCY WARNING] Stock mismatch! Expected: " + expectedStock + ", Got: " + finalStock);
        }

        // ========== PERSISTENCE SUMMARY ==========
        waitForUser("PERSISTENCE LAYER DEMONSTRATION");
        persistence.saveConfiguration(config);
        persistence.printTransactionSummary();

        System.out.println("\n=== Simulation Complete ===");
        System.out.println("Thank you for running the Aura Retail OS simulation!");
        scanner.close();
    }
}
