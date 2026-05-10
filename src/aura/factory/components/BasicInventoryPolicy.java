package aura.factory.components;

import aura.domain.Product;
import aura.state.KioskState;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Concrete Product — default InventoryPolicy
 *    - Intent    : Standard purchase eligibility rules: state must allow
 *                  purchases, quantity must be positive, and available
 *                  stock must be sufficient. Used by Pharmacy and Food
 *                  factories.
 *
 * 2. STATE (Behavioural) — indirect participation
 *    - Delegates the operational check to KioskState.canPurchase(),
 *      so the same policy class works correctly in Active, PowerSaving,
 *      Maintenance, and EmergencyLockdown modes without any
 *      conditional branching on state type.
 * ============================================================
 */
// Design Pattern: Abstract Factory (Concrete Product — BasicInventoryPolicy)
//                 State (indirect — delegates to KioskState)
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
