package aura.events;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. OBSERVER (Behavioural)
 *    - Role      : Concrete Event (notification payload)
 *    - Intent    : Notifies subscribers that a hardware module has
 *                  failed (e.g. a dispenser motor fault). Carries the
 *                  hardware ID and a human-readable detail string.
 *    - Published by: KioskFacade.purchaseItem() on a ROLLED_BACK result,
 *                  and from AuraConsoleApp when an operator reports a
 *                  fault via the hardware console.
 *    - Priority  : EventPriority.HIGH — dispatched before NORMAL events
 *                  but after EMERGENCY events in a publishBatch() call.
 * ============================================================
 */
// Design Pattern: Observer (Concrete Event)
public class HardwareFailureEvent extends AbstractSystemEvent {
    private final String hardwareId;
    private final String detail;

    public HardwareFailureEvent(String hardwareId, String detail) {
        this.hardwareId = hardwareId;
        this.detail = detail;
    }

    public String hardwareId() {
        return hardwareId;
    }

    @Override
    public String type() {
        return "HARDWARE_FAILURE";
    }

    @Override
    public EventPriority priority() {
        return EventPriority.HIGH;
    }

    @Override
    public String message() {
        return hardwareId + " failure: " + detail;
    }
}
