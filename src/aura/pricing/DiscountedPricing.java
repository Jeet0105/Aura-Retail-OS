package aura.pricing;

import aura.domain.Product;

// Design Pattern: Strategy
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
