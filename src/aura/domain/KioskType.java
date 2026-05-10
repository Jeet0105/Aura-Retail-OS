package aura.domain;

public enum KioskType {
    PHARMACY("Pharmacy"),
    FOOD("Food"),
    EMERGENCY_RELIEF("Emergency Relief");

    private final String label;

    KioskType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
