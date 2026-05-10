package aura.factory.components;

import aura.domain.Product;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Abstract Product — "VerificationModule" product line
 *    - Intent    : Declares the interface for user/product verification
 *                  logic. Each factory creates its own inner implementation
 *                  (PrescriptionVerification, BasicPaymentVerification,
 *                  RationIdentityVerification).
 *    - Why here  : PurchaseCommand depends only on this interface, so
 *                  kiosk-specific verification rules are interchangeable
 *                  without modifying the command logic.
 * ============================================================
 */
// Design Pattern: Abstract Factory (Abstract Product — VerificationModule)
public interface VerificationModule {
    String name();

    boolean verify(String userId, Product product);
}
