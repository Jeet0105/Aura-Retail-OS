package aura.events;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. OBSERVER (Behavioural)
 *    - Role      : Concrete Event (notification payload)
 *    - Intent    : Carries the product name, current available count,
 *                  and low-stock threshold to all subscribers registered
 *                  for "LOW_STOCK" events via EventBus.
 *    - Published by: KioskFacade.purchaseItem() when post-purchase
 *                  available stock drops to or below the threshold.
 *    - Priority  : EventPriority.NORMAL — dispatched after EMERGENCY
 *                  and HIGH events in a publishBatch() call.
 * ============================================================
 */
// Design Pattern: Observer (Concrete Event)
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
