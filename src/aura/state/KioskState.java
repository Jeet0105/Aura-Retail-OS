package aura.state;

public interface KioskState {
    String name();

    boolean canPurchase();

    boolean canRestock();

    String operationalNote();
}
