package failure;

/**
 * Design Pattern: Chain of Responsibility (Concrete Handler)
 * First handler in chain - attempts automatic retries before escalating.
 */
public class RetryHandler extends FailureHandler {
    private final int MAX_RETRIES = 3;

    @Override
    public void handleFailure(String failureContext) {
        System.out.println("[RetryHandler] Initiating retry sequence for: " + failureContext);
        
        // Retry loop - attempt up to MAX_RETRIES times
        for (int i = 1; i <= MAX_RETRIES; i++) {
            System.out.println("[RetryHandler] Retry attempt " + i + " of " + MAX_RETRIES);
            // Simulate retry attempt (in real system, this would retry the operation)
            try {
                Thread.sleep(100); // Simulate retry delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("[RetryHandler] All " + MAX_RETRIES + " retries exhausted. Escalating to next handler.");
        if (nextHandler != null) {
            nextHandler.handleFailure(failureContext);
        } else {
            System.out.println("[RetryHandler] No further handlers - failure unresolved.");
        }
    }
}
