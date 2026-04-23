package strategy;

/**
 * Design Pattern: Strategy (Concrete Strategy)
 * Standard pricing with no modifications.
 */
public class StandardPricing implements PricingStrategy {
    @Override
    public double calculateFinalPrice(double basePrice) {
        return basePrice;
    }
}
