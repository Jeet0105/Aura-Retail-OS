package aura.state;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STATE (Behavioural)
 *    - Role      : Context — holds and delegates to the current state
 *    - Intent    : Maintains a reference to the current KioskState and
 *                  exposes transitionTo() so KioskFacade can change
 *                  the operational mode at runtime without any
 *                  conditional logic in the context class.
 *    - Default state: ActiveMode (normal customer operations).
 *    - How       : state() returns the current state for read-only
 *                  queries; transitionTo() atomically replaces it.
 *    - Clients   : KioskFacade reads state().canPurchase() and
 *                  state().canRestock() to gate operations.
 * ============================================================
 */
// Design Pattern: State (Context)
public class KioskContext {
    private KioskState state = new ActiveMode();

    public KioskState state() {
        return state;
    }

    public void transitionTo(KioskState next) {
        state = next;
    }
}
