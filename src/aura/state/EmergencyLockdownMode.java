package aura.state;

// Design Pattern: State
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
}
