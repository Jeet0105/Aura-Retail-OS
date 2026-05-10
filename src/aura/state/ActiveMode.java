package aura.state;

// Design Pattern: State
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
}
