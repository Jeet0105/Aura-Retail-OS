package aura.pricing;

import aura.domain.Product;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STRATEGY (Behavioural)
 *    - Role      : Concrete Strategy — standard (full-price) pricing
 *    - Intent    : Implements the simplest pricing algorithm:
 *                  finalPrice = basePrice × quantity. No discounts.
 *    - Used by   : PharmacyKioskFactory as its default pricing.
 *                  Can also be set at runtime via KioskFacade.setPricing().
 * ============================================================
 */
// Design Pattern: Strategy (Concrete Strategy — Standard Pricing)
public class StandardPricing implements PricingStrategy {
    @Override
    public String name() {
        return "Standard";
    }

    @Override
    public double price(Product product, int quantity) {
        return product.getBasePrice() * quantity;
    }
}
