package state;

/**
 * Design Pattern: State
 * Interface defining the behavioral states of a kiosk.
 */
public interface KioskState {
    void handlePurchase(KioskContext context);
    void handleMaintenance(KioskContext context);
}
