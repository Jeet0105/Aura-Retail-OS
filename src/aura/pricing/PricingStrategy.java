package aura.pricing;

import aura.domain.Product;

public interface PricingStrategy {
    String name();

    double price(Product product, int quantity);
}
