package aura.events;

public class LowStockEvent extends AbstractSystemEvent {
    private final String productName;
    private final int available;
    private final int threshold;

    public LowStockEvent(String productName, int available, int threshold) {
        this.productName = productName;
        this.available = available;
        this.threshold = threshold;
    }

    @Override
    public String type() {
        return "LOW_STOCK";
    }

    @Override
    public EventPriority priority() {
        return EventPriority.NORMAL;
    }

    @Override
    public String message() {
        return productName + " is low: " + available + " available, threshold " + threshold;
    }
}
