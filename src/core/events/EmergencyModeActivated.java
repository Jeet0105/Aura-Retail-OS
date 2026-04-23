package core.events;

/**
 * Design Pattern: Observer (Concrete Event)
 * Event triggered when emergency mode is activated system-wide.
 */
public class EmergencyModeActivated implements SystemEvent {
    private String reason;

    public EmergencyModeActivated(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }

    @Override
    public String getEventType() {
        return "EMERGENCY_MODE";
    }
}
