package factory;

/**
 * Design Pattern: Abstract Factory (Concrete Factory)
 * Creates components for emergency relief kiosks with rationing support.
 */

import factory.components.*;

public class EmergencyReliefKioskFactory implements KioskFactory {
    @Override
    public Dispenser createDispenser() {
        return new BulkDispenser();
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new RationingVerificationModule();
    }

    @Override
    public InventoryPolicy createInventoryPolicy() {
        return new EmergencyRationInventoryPolicy();
    }

    static class BulkDispenser implements Dispenser {
        public void dispenseProduct(String productId) {
            System.out.println("[Emergency] Dispensing supplies in bulk: " + productId);
        }
    }

    static class RationingVerificationModule implements VerificationModule {
        public boolean verify(String userId, String productId) {
            System.out.println("[Emergency] Verifying rationing limits for user " + userId);
            return true;
        }
    }

    static class EmergencyRationInventoryPolicy implements InventoryPolicy {
        public boolean canPurchase(String productId, int amount, int stock) {
            System.out.println("[Emergency] Checking emergency ration limits for item " + productId);
            return stock >= amount && amount <= 2; // limit to 2 per person constraint
        }
    }
}
