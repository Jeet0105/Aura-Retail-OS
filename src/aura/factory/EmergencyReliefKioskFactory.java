package aura.factory;

import aura.domain.KioskType;
import aura.domain.Product;
import aura.factory.components.BaseDispenser;
import aura.factory.components.Dispenser;
import aura.factory.components.EmergencyRationPolicy;
import aura.factory.components.InventoryPolicy;
import aura.factory.components.VerificationModule;
import aura.pricing.EmergencyPricing;
import aura.pricing.PricingStrategy;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Concrete Factory for the Emergency Relief kiosk family
 *    - Intent    : Produces a cohesive set of emergency-specific components:
 *                    - BaseDispenser configured as "Bulk Relief Dispenser"
 *                    - RationIdentityVerification (inner class, requires
 *                      userId ≥ 3 chars)
 *                    - EmergencyRationPolicy (caps each purchase at 2 units)
 *                    - EmergencyPricing (50 % subsidy on base price)
 *    - Why here  : Emergency kiosk constraints (ration limits, identity
 *                  checks, subsidised pricing) are fully encapsulated in
 *                  this factory, leaving the rest of the system unchanged.
 * ============================================================
 */
// Design Pattern: Abstract Factory (Concrete Factory — Emergency Relief)
public class EmergencyReliefKioskFactory implements KioskFactory {
    @Override
    public KioskType type() {
        return KioskType.EMERGENCY_RELIEF;
    }

    @Override
    public Dispenser createDispenser() {
        return new BaseDispenser("Bulk Relief Dispenser", "Releasing ration unit");
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new RationIdentityVerification();
    }

    @Override
    public InventoryPolicy createInventoryPolicy() {
        return new EmergencyRationPolicy(2);
    }

    @Override
    public PricingStrategy createDefaultPricing() {
        return new EmergencyPricing();
    }

    private static class RationIdentityVerification implements VerificationModule {
        @Override
        public String name() {
            return "Ration Identity Verification";
        }

        @Override
        public boolean verify(String userId, Product product) {
            return userId != null && userId.trim().length() >= 1;
        }
    }
}
