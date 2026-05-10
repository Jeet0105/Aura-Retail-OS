package aura.state;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STATE (Behavioural)
 *    - Role      : Concrete State — Power Saving mode
 *    - Intent    : Represents a reduced-power kiosk: purchases are
 *                  still allowed (with potentially delayed hardware),
 *                  but restocking is locked to conserve energy.
 *    - Behaviour : canPurchase() = true, canRestock() = false.
 *    - Demo link : BaseDispenser simulates hardware delay via
 *                  Thread.sleep when delayedHardware=true, modelling
 *                  the slower hardware response of this state.
 * ============================================================
 */
// Design Pattern: State (Concrete State — Power Saving)
public class PowerSavingMode implements KioskState {
    @Override
    public String name() {
        return "Power Saving";
    }

    @Override
    public boolean canPurchase() {
        return true;
    }

    @Override
    public boolean canRestock() {
        return false;
    }

    @Override
    public String operationalNote() {
        return "Purchases allowed with slower hardware response; restock locked.";
    }
}
