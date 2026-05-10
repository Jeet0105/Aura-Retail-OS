package aura.failure;

public class RetryHandler extends FailureHandler {
    @Override
    protected boolean attempt(FailureReport report) {
        System.out.println("  Recovery: retrying " + report.source() + " three times...");
        for (int i = 1; i <= 3; i++) {
            System.out.println("    retry " + i + " failed");
        }
        return false;
    }
}
