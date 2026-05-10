package aura.events;

import java.time.LocalDateTime;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. OBSERVER (Behavioural)
 *    - Role      : Event (notification object interface)
 *    - Intent    : Provides the common contract for all events
 *                  published through the EventBus. Each event carries
 *                  a type(), priority(), message(), and timestamp so
 *                  observers can react appropriately.
 *    - Concrete implementations: LowStockEvent, HardwareFailureEvent,
 *                  EmergencyModeEvent.
 *    - Why here  : Typing events through a shared interface lets
 *                  EventBus remain generic and lets priority-based
 *                  batch dispatch work without casting.
 * ============================================================
 */
// Design Pattern: Observer (Event interface)
public interface SystemEvent {
    String type();

    EventPriority priority();

    String message();

    LocalDateTime occurredAt();
}
