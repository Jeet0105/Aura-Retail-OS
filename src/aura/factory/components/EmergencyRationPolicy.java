package aura.factory.components;

import aura.domain.Product;
import aura.state.KioskState;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Concrete Product — emergency InventoryPolicy
 *    - Intent    : Extends BasicInventoryPolicy to add an additional
 *                  per-transaction unit cap (maxQuantity = 2) that
 *                  prevents over-purchasing during emergency operations.
 *                  Applied to all kiosk families while system emergency
 *                  mode is active (AuraConsoleApp EMERGENCY_MODE handler);
 *                  EmergencyReliefKioskFactory also uses it as the default
 *                  policy for the relief kiosk family.
 *
 * 2. STATE (Behavioural) — indirect participation (via super)
 *    - Inherits the KioskState.canPurchase() delegation from
 *      BasicInventoryPolicy, so emergency ration rules layer on top
 *      of state-based operational checks automatically.
 * ============================================================
 */
// Design Pattern: Abstract Factory (Concrete Product — EmergencyRationPolicy)
//                 State (indirect — via BasicInventoryPolicy delegation)
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
