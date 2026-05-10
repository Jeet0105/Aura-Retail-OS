package aura.pricing;

import aura.domain.Product;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STRATEGY (Behavioural)
 *    - Role      : Concrete Strategy — emergency relief pricing
 *    - Intent    : Implements a fixed emergency pricing algorithm
 *                  (50% subsidy of the base price).
 *    - Used by   : EmergencyReliefKioskFactory as its default pricing.
 *                  Also applied to all kiosks at runtime when
 *                  EmergencyModeEvent is broadcasted.
 * ============================================================
 */
// Design Pattern: Strategy (Concrete Strategy — Emergency Pricing)
public class EmergencyPricing implements PricingStrategy {
    @Override
    public String name() {
        return "Emergency Relief";
    }

    @Override
    public double price(Product product, int quantity) {
        return product.getBasePrice() * quantity * 0.50;
    }
}
