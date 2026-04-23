package strategy;

/**
 * Design Pattern: Strategy (Context)
 * Executes the currently selected pricing strategy.
 */
public class PricingContext {
    private PricingStrategy strategy;

    public PricingContext(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public double executePricing(double basePrice) {
        return strategy.calculateFinalPrice(basePrice);
    }

    public String getCurrentStrategy() {
        return strategy.getClass().getSimpleName();
    }
}
