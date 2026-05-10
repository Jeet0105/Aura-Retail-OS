package aura.pricing;

import aura.domain.Product;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STRATEGY (Behavioural)
 *    - Role      : Concrete Strategy — discounted pricing
 *    - Intent    : Implements a pricing algorithm that applies a
 *                  percentage discount to the base price.
 *    - Parameterisation: Takes the discount percentage at construction time,
 *                  so a single class can represent any discount level
 *                  (e.g. 5% for FoodKioskFactory's default pricing).
 * ============================================================
 */
// Design Pattern: Strategy (Concrete Strategy — Discounted Pricing)
public class DiscountedPricing implements PricingStrategy {
    private final double percentage;

    public DiscountedPricing(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String name() {
        return "Discount " + String.format("%.0f", percentage) + "%";
    }

    @Override
    public double price(Product product, int quantity) {
        double base = product.getBasePrice() * quantity;
        return base - (base * percentage / 100.0);
    }
}
