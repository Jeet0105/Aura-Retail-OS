package aura.factory.components;

import aura.domain.Product;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Abstract Product — "Dispenser" product line
 *    - Intent    : Declares the interface for all dispenser types
 *                  created by KioskFactory implementations. Clients
 *                  (e.g. PurchaseCommand) depend only on this interface,
 *                  not on any concrete dispenser class.
 *    - Concrete products: BaseDispenser (shared implementation used by
 *                  all three factory families, configured via constructor
 *                  arguments).
 * ============================================================
 */
// Design Pattern: Abstract Factory (Abstract Product — Dispenser)
public interface Dispenser {
    String name();

    void dispense(Product product, int quantity, boolean delayed, boolean forceFailure) throws DispenseException;
}
