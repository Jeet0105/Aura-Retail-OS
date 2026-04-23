package failure;

/**
 * Design Pattern: Chain of Responsibility (Abstract Handler)
 * Abstract base class for failure handling chain.
 */
public abstract class FailureHandler {
    protected FailureHandler nextHandler;

    public void setNextHandler(FailureHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void handleFailure(String failureContext);
}
