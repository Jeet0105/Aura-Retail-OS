package aura.factory.components;

import aura.domain.Product;
import aura.state.KioskState;

public class BasicInventoryPolicy implements InventoryPolicy {
    @Override
    public String name() {
        return "Basic Stock Policy";
    }

    @Override
    public boolean canPurchase(Product product, int quantity, int availableStock, KioskState state) {
        return state.canPurchase() && quantity > 0 && availableStock >= quantity;
    }

    @Override
    public String denialReason(Product product, int quantity, int availableStock, KioskState state) {
        if (!state.canPurchase()) {
            return "kiosk state blocks purchases";
        }
        if (quantity <= 0) {
            return "quantity must be positive";
        }
        return "insufficient derived available stock";
    }
}
