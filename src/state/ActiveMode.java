package state;

/**
 * Design Pattern: State (Concrete State)
 * Normal operational mode where purchases are processed.
 */
public class ActiveMode implements KioskState {
    @Override
    public void handlePurchase(KioskContext context) {
        System.out.println("[ActiveMode] Handling purchase normally.");
    }
    @Override
    public void handleMaintenance(KioskContext context) {
        System.out.println("[ActiveMode] Entering maintenance mode.");
        context.setState(new MaintenanceMode());
    }
}
