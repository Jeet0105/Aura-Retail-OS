package failure;

/**
 * Design Pattern: Chain of Responsibility (Concrete Handler)
 * Final handler in chain - alerts human technicians when automatic recovery fails.
 */

public class TechnicianAlertHandler extends FailureHandler {
    @Override
    public void handleFailure(String failureContext) {
        System.out.println("[TechnicianAlertHandler] CRITICAL: Sending alert to city technicians for: " + failureContext);
    }
}
