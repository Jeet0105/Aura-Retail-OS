package aura.events;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. OBSERVER (Behavioural)
 *    - Role      : Concrete Event (notification payload)
 *    - Intent    : Signals all kiosks and subsystems that the system
 *                  has entered emergency mode. The AuraConsoleApp
 *                  subscriber transitions all kiosks to
 *                  EmergencyLockdownMode and switches pricing to
 *                  EmergencyPricing upon receiving this event.
 *    - Published by: AuraConsoleApp.emergencyBroadcast() via a
 *                  publishBatch() call that demonstrates priority-ordered
 *                  dispatch.
 *    - Priority  : EventPriority.EMERGENCY — always dispatched first
 *                  regardless of batch insertion order.
 * ============================================================
 */
// Design Pattern: Observer (Concrete Event)
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
