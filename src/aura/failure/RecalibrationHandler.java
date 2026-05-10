package aura.failure;

public class RecalibrationHandler extends FailureHandler {
    @Override
    protected boolean attempt(FailureReport report) {
        System.out.println("  Recovery: recalibrating actuator for " + report.source() + "...");
        return false;
    }
}
