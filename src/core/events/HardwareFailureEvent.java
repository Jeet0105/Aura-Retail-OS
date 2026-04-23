package core.events;

/**
 * Design Pattern: Observer (Concrete Event)
 * Event triggered when a hardware component fails.
 */
public class HardwareFailureEvent implements SystemEvent {
    private String componentName;
    private String errorMessage;

    public HardwareFailureEvent(String componentName, String errorMessage) {
        this.componentName = componentName;
        this.errorMessage = errorMessage;
    }

    public String getComponentName() { return componentName; }
    public String getErrorMessage() { return errorMessage; }

    @Override
    public String getEventType() {
        return "HARDWARE_FAILURE";
    }
}
