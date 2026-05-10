package aura.pricing;

import aura.domain.Product;

// Design Pattern: Strategy
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
