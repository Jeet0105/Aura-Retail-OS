package factory;

import factory.components.Dispenser;
import factory.components.InventoryPolicy;
import factory.components.VerificationModule;

/**
 * Design Pattern: Abstract Factory
 * Interface for creating kiosk-specific component families.
 */
public interface KioskFactory {
    Dispenser createDispenser();
    VerificationModule createVerificationModule();
    InventoryPolicy createInventoryPolicy();
}
