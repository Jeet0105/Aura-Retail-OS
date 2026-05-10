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

// Design Pattern: Abstract Factory
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
