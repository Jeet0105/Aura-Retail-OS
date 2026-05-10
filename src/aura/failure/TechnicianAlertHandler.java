package aura.failure;

public class TechnicianAlertHandler extends FailureHandler {
    @Override
    protected boolean attempt(FailureReport report) {
        System.out.println("  Recovery: technician alert opened for " + report.source() + " (" + report.detail() + ")");
        return true;
    }
}
