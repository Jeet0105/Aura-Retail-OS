package aura.factory.components;

import aura.domain.Product;
import aura.state.KioskState;

public class EmergencyRationPolicy extends BasicInventoryPolicy {
    private final int maxQuantity;

    public EmergencyRationPolicy(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    @Override
    public String name() {
        return "Emergency Ration Policy";
    }

    @Override
    public boolean canPurchase(Product product, int quantity, int availableStock, KioskState state) {
        return quantity <= maxQuantity && super.canPurchase(product, quantity, availableStock, state);
    }

    @Override
    public String denialReason(Product product, int quantity, int availableStock, KioskState state) {
        if (quantity > maxQuantity) {
            return "emergency ration limit is " + maxQuantity + " units";
        }
        return super.denialReason(product, quantity, availableStock, state);
    }
}
