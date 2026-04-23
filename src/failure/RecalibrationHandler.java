package failure;

/**
 * Design Pattern: Chain of Responsibility (Concrete Handler)
 * Second handler in chain - attempts hardware recalibration.
 */

public class RecalibrationHandler extends FailureHandler {
    @Override
    public void handleFailure(String failureContext) {
        System.out.println("[RecalibrationHandler] Attempting hardware recalibration for: " + failureContext);
        System.out.println("[RecalibrationHandler] Recalibration failed. Passing to next handler.");
        if (nextHandler != null) {
            nextHandler.handleFailure(failureContext);
        }
    }
}
