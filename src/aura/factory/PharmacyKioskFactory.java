package aura.factory;

import aura.domain.KioskType;
import aura.domain.Product;
import aura.factory.components.BaseDispenser;
import aura.factory.components.BasicInventoryPolicy;
import aura.factory.components.Dispenser;
import aura.factory.components.InventoryPolicy;
import aura.factory.components.VerificationModule;
import aura.pricing.PricingStrategy;
import aura.pricing.StandardPricing;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Concrete Factory for the Pharmacy kiosk family
 *    - Intent    : Produces a cohesive set of pharmacy-specific
 *                  components that are guaranteed to work together:
 *                    - BaseDispenser configured as "Secure Medication Dispenser"
 *                    - PrescriptionVerification (inner class)
 *                    - BasicInventoryPolicy (standard stock rules)
 *                    - StandardPricing (full-price medication)
 *    - Why here  : All pharmacy component decisions (which dispenser,
 *                  which verifier, which pricing) are centralised here.
 *                  KioskFacade and other clients only depend on the
 *                  KioskFactory interface, not on this class.
 * ============================================================
 */
// Design Pattern: Abstract Factory (Concrete Factory — Pharmacy)
public class PharmacyKioskFactory implements KioskFactory {
    @Override
    public KioskType type() {
        return KioskType.PHARMACY;
    }

    @Override
    public Dispenser createDispenser() {
        return new BaseDispenser("Secure Medication Dispenser", "Securely dispensing");
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new PrescriptionVerification();
    }

    @Override
    public InventoryPolicy createInventoryPolicy() {
        return new BasicInventoryPolicy();
    }

    @Override
    public PricingStrategy createDefaultPricing() {
        return new StandardPricing();
    }

    private static class PrescriptionVerification implements VerificationModule {
        @Override
        public String name() {
            return "Prescription Verification";
        }

        @Override
        public boolean verify(String userId, Product product) {
            return userId != null && !userId.trim().isEmpty();
        }
    }
}
