package aura.state;

// Design Pattern: State
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
