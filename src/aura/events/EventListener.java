package aura.events;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. OBSERVER (Behavioural)
 *    - Role      : Observer (subscriber / listener interface)
 *    - Intent    : Defines the contract that every event subscriber
 *                  must implement. Any class that wants to react to
 *                  system events implements this interface and
 *                  registers itself with EventBus.
 *    - How       : Single-method interface (functional interface)
 *                  onEvent(SystemEvent) is called by EventBus.dispatch()
 *                  whenever a matching event is published.
 *    - Why here  : Keeping the observer contract in its own interface
 *                  decouples the subscriber's implementation from the
 *                  EventBus mechanics. Lambda expressions in
 *                  AuraConsoleApp use this interface directly.
 * ============================================================
 */
// Design Pattern: Observer (Listener / Subscriber interface)
public interface EventListener {
    void onEvent(SystemEvent event);
}
