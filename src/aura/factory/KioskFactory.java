package aura.factory;

import aura.domain.KioskType;
import aura.factory.components.Dispenser;
import aura.factory.components.InventoryPolicy;
import aura.factory.components.VerificationModule;
import aura.pricing.PricingStrategy;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Abstract Factory interface
 *    - Intent    : Declares the creation contract for a family of
 *                  related kiosk components (Dispenser,
 *                  VerificationModule, InventoryPolicy, PricingStrategy)
 *                  without specifying their concrete classes.
 *    - Concrete factories: PharmacyKioskFactory, FoodKioskFactory,
 *                  EmergencyReliefKioskFactory.
 *    - Why here  : Each kiosk type needs a compatible, pre-configured
 *                  set of components. The factory hides construction
 *                  details and guarantees component compatibility within
 *                  a family (e.g. pharmacy always gets prescription
 *                  verification + medication lockbox awareness).
 *    - Extensibility: Adding a new kiosk type requires only a new
 *                  implementing class — no changes to KioskFacade or
 *                  the rest of the system (Open/Closed Principle).
 * ============================================================
 */
// Design Pattern: Abstract Factory (interface)
public interface KioskFactory {
    KioskType type();

    Dispenser createDispenser();

    VerificationModule createVerificationModule();

    InventoryPolicy createInventoryPolicy();

    PricingStrategy createDefaultPricing();
}
