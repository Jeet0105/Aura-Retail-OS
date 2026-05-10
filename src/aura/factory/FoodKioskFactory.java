package aura.factory;

import aura.domain.KioskType;
import aura.domain.Product;
import aura.factory.components.BaseDispenser;
import aura.factory.components.BasicInventoryPolicy;
import aura.factory.components.Dispenser;
import aura.factory.components.InventoryPolicy;
import aura.factory.components.VerificationModule;
import aura.pricing.DiscountedPricing;
import aura.pricing.PricingStrategy;

// Design Pattern: Abstract Factory
public class FoodKioskFactory implements KioskFactory {
    @Override
    public KioskType type() {
        return KioskType.FOOD;
    }

    @Override
    public Dispenser createDispenser() {
        return new BaseDispenser("Temperature-Aware Food Dispenser", "Serving");
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new BasicPaymentVerification();
    }

    @Override
    public InventoryPolicy createInventoryPolicy() {
        return new BasicInventoryPolicy();
    }

    @Override
    public PricingStrategy createDefaultPricing() {
        return new DiscountedPricing(5);
    }

    private static class BasicPaymentVerification implements VerificationModule {
        @Override
        public String name() {
            return "Payment Verification";
        }

        @Override
        public boolean verify(String userId, Product product) {
            return userId != null && !userId.trim().isEmpty();
        }
    }
}
