package state;

/**
 * Design Pattern: State (Concrete State)
 * Maintenance mode where purchases are disabled for servicing.
 */
public class MaintenanceMode implements KioskState {
    @Override
    public void handlePurchase(KioskContext context) {
        System.out.println("[MaintenanceMode] Sorry, kiosk is under maintenance.");
    }
    @Override
    public void handleMaintenance(KioskContext context) {
        System.out.println("[MaintenanceMode] Already in maintenance mode.");
    }
}
