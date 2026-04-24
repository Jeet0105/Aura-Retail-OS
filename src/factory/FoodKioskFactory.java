package factory;

import factory.components.*;
import strategy.PricingStrategy;
import strategy.DiscountedPricing;

/**
 * Design Pattern: Abstract Factory (Concrete Factory)
 * Creates components for food kiosks with refrigeration support.
 */
public class FoodKioskFactory implements KioskFactory {
    @Override
    public Dispenser createDispenser() {
        return new RefrigeratedDispenser();
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new BasicPaymentVerification();
    }

    @Override
    public InventoryPolicy createInventoryPolicy() {
        return new PerishableInventoryPolicy();
    }

    @Override
    public PricingStrategy createPricingModule() {
        // Food kiosks offer 10% discount on perishable items
        return new DiscountedPricing(10);
    }

    static class RefrigeratedDispenser implements Dispenser {
        public void dispenseProduct(String productId) {
            System.out.println("[Food] Dispensing food/beverage from refrigerated unit: " + productId);
        }
    }

    static class BasicPaymentVerification implements VerificationModule {
        public boolean verify(String userId, String productId) {
            System.out.println("[Food] Basic verification passed.");
            return true;
        }
    }

    static class PerishableInventoryPolicy implements InventoryPolicy {
        public boolean canPurchase(String productId, int amount, int stock) {
            return stock >= amount;
        }
    }
}
