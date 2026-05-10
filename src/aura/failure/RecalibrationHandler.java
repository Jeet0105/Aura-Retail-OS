package aura.failure;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. CHAIN OF RESPONSIBILITY (Behavioural)
 *    - Role      : Concrete Handler (Second in chain)
 *    - Intent    : Provides a more advanced recovery attempt
 *                  (hardware recalibration) if the basic retry fails.
 *    - Chain logic: Returns false in this simulation to pass the
 *                  failure along to the final fallback handler.
 * ============================================================
 */
// Design Pattern: Chain of Responsibility (Concrete Handler — Recalibration)
public class RecalibrationHandler extends FailureHandler {
    @Override
    protected boolean attempt(FailureReport report) {
        System.out.println("  Recovery: recalibrating actuator for " + report.source() + "...");
        return false;
    }
}
