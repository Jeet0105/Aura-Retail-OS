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

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Concrete Factory for the Food kiosk family
 *    - Intent    : Produces a cohesive set of food-specific components:
 *                    - BaseDispenser configured as "Temperature-Aware Food Dispenser"
 *                    - BasicPaymentVerification (inner class)
 *                    - BasicInventoryPolicy (standard stock rules)
 *                    - DiscountedPricing at 5 % (food loyalty discount)
 *    - Why here  : All food kiosk component decisions are encapsulated
 *                  here. Switching the food dispenser or pricing strategy
 *                  requires only a change in this class.
 * ============================================================
 */
// Design Pattern: Abstract Factory (Concrete Factory — Food)
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
