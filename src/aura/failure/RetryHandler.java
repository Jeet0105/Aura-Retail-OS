package aura.failure;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. CHAIN OF RESPONSIBILITY (Behavioural)
 *    - Role      : Concrete Handler (First in chain)
 *    - Intent    : Attempts to recover from a failure by performing
 *                  a simple software retry loop.
 *    - Chain logic: Always returns false (in this simulation) to
 *                  escalate the issue down the chain if simple
 *                  retries fail to resolve the hardware fault.
 * ============================================================
 */
// Design Pattern: Chain of Responsibility (Concrete Handler — Retry)
public class RetryHandler extends FailureHandler {
    @Override
    protected boolean attempt(FailureReport report) {
        System.out.println("  Recovery: retrying " + report.source() + " three times...");
        for (int i = 1; i <= 3; i++) {
            System.out.println("    retry " + i + " failed");
        }
        return false;
    }
}
