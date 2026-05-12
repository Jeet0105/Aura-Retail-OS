package aura.state;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STATE (Behavioural)
 *    - Role      : Concrete State — Emergency Lockdown mode
 *    - Intent    : Represents highest-priority emergency operations.
 *                  Both purchases (ration-safe only, gated by
 *                  EmergencyRationPolicy) and restocking are allowed,
 *                  but pricing is automatically switched to
 *                  EmergencyPricing (50 % subsidy) by the
 *                  EmergencyModeEvent subscriber.
 *    - Behaviour : canPurchase() = true, canRestock() = true.
 *    - Activation: Triggered by EmergencyModeEvent subscriber in
 *                  AuraConsoleApp which calls setState(new EmergencyLockdownMode())
 *                  on all registered kiosks simultaneously.
 * ============================================================
 */
// Design Pattern: State (Concrete State — Emergency Lockdown)
public class EmergencyLockdownMode implements KioskState {
    @Override
    public String name() {
        return "Emergency Lockdown";
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
        return "Only ration-safe purchases are allowed; emergency priority is active.";
    }

    @Override
    public boolean delayedHardware() {
        return false;
    }
}
