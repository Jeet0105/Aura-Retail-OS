package aura.factory.components;

import aura.domain.Product;
import aura.state.KioskState;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Abstract Product — "InventoryPolicy" product line
 *    - Intent    : Declares the contract that governs whether a purchase
 *                  is allowed given stock, quantity, and current kiosk
 *                  state. Factories provide type-specific implementations:
 *                    - BasicInventoryPolicy (Pharmacy, Food)
 *                    - EmergencyRationPolicy (Emergency Relief, max 2 units)
 *    - Note      : Also participates in the STATE pattern since
 *                  canPurchase() receives the current KioskState and
 *                  delegates the operational check to it.
 * ============================================================
 */
// Design Pattern: Abstract Factory (Abstract Product — InventoryPolicy)
public interface InventoryPolicy {
    String name();

    boolean canPurchase(Product product, int quantity, int availableStock, KioskState state);

    String denialReason(Product product, int quantity, int availableStock, KioskState state);
}
