package factory;

import factory.components.Dispenser;
import factory.components.InventoryPolicy;
import factory.components.VerificationModule;
import strategy.PricingStrategy;

/**
 * Design Pattern: Abstract Factory
 * Interface for creating kiosk-specific component families.
 * Each factory creates a complete set of compatible components including
 * Dispenser, VerificationModule, InventoryPolicy, and PricingModule.
 */
public interface KioskFactory {
    Dispenser createDispenser();
    VerificationModule createVerificationModule();
    InventoryPolicy createInventoryPolicy();
    PricingStrategy createPricingModule();
}
