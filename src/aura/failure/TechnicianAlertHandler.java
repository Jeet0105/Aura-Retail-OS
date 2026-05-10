package aura.failure;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. CHAIN OF RESPONSIBILITY (Behavioural)
 *    - Role      : Concrete Handler (Final fallback in chain)
 *    - Intent    : Acts as the terminal handler when all automated
 *                  recovery attempts have failed. Opens a technician
 *                  alert ticket to dispatch a human.
 *    - Chain logic: Always returns true, signalling that the failure
 *                  has been "handled" (logged and escalated) and should
 *                  not propagate further.
 * ============================================================
 */
// Design Pattern: Chain of Responsibility (Concrete Handler — Technician Alert)
public class TechnicianAlertHandler extends FailureHandler {
    @Override
    protected boolean attempt(FailureReport report) {
        System.out.println("  Recovery: technician alert opened for " + report.source() + " (" + report.detail() + ")");
        return true;
    }
}
