package aura.events;

public class EmergencyModeEvent extends AbstractSystemEvent {
    private final String reason;

    public EmergencyModeEvent(String reason) {
        this.reason = reason;
    }

    @Override
    public String type() {
        return "EMERGENCY_MODE";
    }

    @Override
    public EventPriority priority() {
        return EventPriority.EMERGENCY;
    }

    @Override
    public String message() {
        return "Emergency mode activated: " + reason;
    }
}
