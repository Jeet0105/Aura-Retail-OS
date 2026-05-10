package aura.events;

import java.time.LocalDateTime;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. OBSERVER (Behavioural)
 *    - Role      : Abstract Concrete Event (shared base for all events)
 *    - Intent    : Provides a default implementation of occurredAt()
 *                  so concrete events (LowStockEvent, HardwareFailureEvent,
 *                  EmergencyModeEvent) do not repeat timestamp logic.
 *    - Why here  : Using an abstract class for the timestamp and leaving
 *                  type/priority/message abstract promotes the DRY
 *                  principle while keeping polymorphic dispatch uniform.
 * ============================================================
 */
// Design Pattern: Observer (Abstract Event base class)
public abstract class AbstractSystemEvent implements SystemEvent {
    private final LocalDateTime occurredAt = LocalDateTime.now();

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}
