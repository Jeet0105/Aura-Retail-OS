package state;

/**
 * Design Pattern: State (Context)
 * Maintains the current kiosk state and delegates behavior to it.
 */
public class KioskContext {
    private KioskState currentState;

    public KioskContext() {
        // Default to active mode
        currentState = new ActiveMode();
    }

    public void setState(KioskState state) {
        System.out.println("[State Change] Kiosk transitions to: " + state.getClass().getSimpleName());
        this.currentState = state;
    }

    public void purchase() {
        currentState.handlePurchase(this);
    }

    public void maintain() {
        currentState.handleMaintenance(this);
    }

    public String getCurrentStateInfo() {
        return currentState.getClass().getSimpleName();
    }
    
    /**
     * Returns true if the current state allows purchases.
     * Only ActiveMode permits normal purchase operations.
     */
    public boolean canPurchase() {
        return currentState instanceof ActiveMode;
    }
}
