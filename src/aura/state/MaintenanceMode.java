package aura.state;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STATE (Behavioural)
 *    - Role      : Concrete State — Maintenance mode
 *    - Intent    : Represents a kiosk taken out of customer service for
 *                  servicing: purchases are disabled so customers cannot
 *                  interact, but technicians can restock.
 *    - Behaviour : canPurchase() = false, canRestock() = true.
 *    - Impact    : BasicInventoryPolicy.canPurchase() returns false
 *                  because it delegates to state.canPurchase(), so no
 *                  conditional code is needed in the policy or facade.
 * ============================================================
 */
// Design Pattern: State (Concrete State — Maintenance)
public class MaintenanceMode implements KioskState {
    @Override
    public String name() {
        return "Maintenance";
    }

    @Override
    public boolean canPurchase() {
        return false;
    }

    @Override
    public boolean canRestock() {
        return true;
    }

    @Override
    public String operationalNote() {
        return "Customer purchases disabled while technicians service the kiosk.";
    }
}
