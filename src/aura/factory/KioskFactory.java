package aura.factory;

import aura.domain.KioskType;
import aura.factory.components.Dispenser;
import aura.factory.components.InventoryPolicy;
import aura.factory.components.VerificationModule;
import aura.pricing.PricingStrategy;

public interface KioskFactory {
    KioskType type();

    Dispenser createDispenser();

    VerificationModule createVerificationModule();

    InventoryPolicy createInventoryPolicy();

    PricingStrategy createDefaultPricing();
}
