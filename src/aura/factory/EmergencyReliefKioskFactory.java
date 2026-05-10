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

// Design Pattern: Abstract Factory
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
            return userId != null && userId.trim().length() >= 3;
        }
    }
}
