package aura.failure;

// Design Pattern: Chain of Responsibility
public abstract class FailureHandler {
    private FailureHandler next;

    public FailureHandler link(FailureHandler next) {
        this.next = next;
        return next;
    }

    public final void handle(FailureReport report) {
        if (!attempt(report) && next != null) {
            next.handle(report);
        }
    }

    protected abstract boolean attempt(FailureReport report);
}
