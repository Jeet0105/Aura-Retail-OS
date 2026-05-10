package aura.events;

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
