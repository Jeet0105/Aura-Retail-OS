package aura.failure;

public class FailureReport {
    private final String source;
    private final String detail;

    public FailureReport(String source, String detail) {
        this.source = source;
        this.detail = detail;
    }

    public String source() {
        return source;
    }

    public String detail() {
        return detail;
    }
}
