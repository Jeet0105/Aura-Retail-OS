package strategy;

/**
 * Design Pattern: Strategy
 * Interface for pricing calculation strategies.
 */
public interface PricingStrategy {
    double calculateFinalPrice(double basePrice);
}
