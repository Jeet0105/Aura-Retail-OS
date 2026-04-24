package factory;

import factory.components.*;
import strategy.PricingStrategy;
import strategy.StandardPricing;

/**
 * Design Pattern: Abstract Factory (Concrete Factory)
 * Creates components for pharmacy kiosks with secure medication handling.
 */
public class PharmacyKioskFactory implements KioskFactory {
    @Override
    public Dispenser createDispenser() {
        return new SecureMedicationDispenser();
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new PrescriptionVerificationModule();
    }

    @Override
    public InventoryPolicy createInventoryPolicy() {
        return new StrictMedicalInventoryPolicy();
    }

    @Override
    public PricingStrategy createPricingModule() {
        // Pharmacy uses standard pricing — no discounts on medications
        return new StandardPricing();
    }

    static class SecureMedicationDispenser implements Dispenser {
        public void dispenseProduct(String productId) {
            System.out.println("[Pharmacy] Dispensing prescription securely: " + productId);
        }
    }

    static class PrescriptionVerificationModule implements VerificationModule {
        public boolean verify(String userId, String productId) {
            System.out.println("[Pharmacy] Verifying prescription for User " + userId);
            return true;
        }
    }

    static class StrictMedicalInventoryPolicy implements InventoryPolicy {
        public boolean canPurchase(String productId, int amount, int stock) {
            return stock >= amount;
        }
    }
}
