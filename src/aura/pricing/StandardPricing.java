package aura.pricing;

import aura.domain.Product;

// Design Pattern: Strategy
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
