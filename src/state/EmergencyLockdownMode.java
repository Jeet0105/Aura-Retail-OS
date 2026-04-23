package state;

/**
 * Design Pattern: State (Concrete State)
 * Emergency lockdown mode with restricted operations and rationing.
 */
public class EmergencyLockdownMode implements KioskState {
    @Override
    public void handlePurchase(KioskContext context) {
        System.out.println("[EmergencyLockdownMode] Handling critical purchase only. Strict limits apply.");
    }
    @Override
    public void handleMaintenance(KioskContext context) {
        System.out.println("[EmergencyLockdownMode] Maintenance forbidden during emergency.");
    }
}
