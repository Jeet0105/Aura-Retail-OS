package aura.state;

// Design Pattern: State
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
