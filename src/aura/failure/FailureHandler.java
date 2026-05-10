package aura.failure;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. CHAIN OF RESPONSIBILITY (Behavioural)
 *    - Role      : Handler (abstract base class)
 *    - Intent    : Decouples the sender of a failure report (KioskFacade)
 *                  from its receivers (RetryHandler, RecalibrationHandler,
 *                  TechnicianAlertHandler) by giving multiple objects a
 *                  chance to handle the failure.
 *    - How       : Defines a link() method to build the chain and a
 *                  Template Method handle() that invokes the concrete
 *                  attempt() method. If attempt() returns false (did not
 *                  fully resolve the issue), handle() automatically passes
 *                  the request to the next handler in the chain.
 * ============================================================
 */
// Design Pattern: Chain of Responsibility (Abstract Handler)
public abstract class FailureHandler {
    private FailureHandler next;

    public FailureHandler link(FailureHandler next) {
        this.next = next;
        return next;
    }

    public final void handle(FailureReport report) {
        if (!attempt(report) && next != null) {
            next.handle(report);
        }
    }

    protected abstract boolean attempt(FailureReport report);
}
