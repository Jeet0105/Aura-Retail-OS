package aura.pricing;

import aura.domain.Product;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STRATEGY (Behavioural)
 *    - Role      : Strategy interface — pricing algorithm contract
 *    - Intent    : Defines the interchangeable pricing algorithm that
 *                  KioskFacade uses to calculate the final price of a
 *                  purchase. Concrete strategies can be swapped at
 *                  runtime without touching the Facade or Command.
 *    - Concrete strategies: StandardPricing, DiscountedPricing,
 *                  EmergencyPricing.
 *    - Context   : KioskFacade holds a PricingStrategy reference and
 *                  exposes setPricing() to switch strategies at runtime.
 *    - Also produced by: KioskFactory.createDefaultPricing() — the
 *                  Abstract Factory sets the initial strategy appropriate
 *                  for each kiosk type.
 * ============================================================
 */
// Design Pattern: Strategy (interface)
public interface PricingStrategy {
    String name();

    double price(Product product, int quantity);
}
