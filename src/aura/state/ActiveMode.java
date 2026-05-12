package aura.state;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STATE (Behavioural)
 *    - Role      : Concrete State — Active (normal operation)
 *    - Intent    : Represents a fully operational kiosk: purchases and
 *                  restocking are both enabled. This is the default
 *                  initial state set by KioskContext.
 *    - Behaviour : canPurchase() = true, canRestock() = true.
 *    - Transition: KioskFacade.setState() or EmergencyModeEvent subscriber
 *                  can transition away from this state at runtime.
 * ============================================================
 */
// Design Pattern: State (Concrete State — Active)
public class ActiveMode implements KioskState {
    @Override
    public String name() {
        return "Active";
    }

    @Override
    public boolean canPurchase() {
        return true;
    }

    @Override
    public boolean canRestock() {
        return true;
    }

    @Override
    public String operationalNote() {
        return "Normal customer operations enabled.";
    }

    @Override
    public boolean delayedHardware() {
        return false;
    }
}
