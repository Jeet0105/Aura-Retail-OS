package core;

import core.events.SystemEvent;

/**
 * Design Pattern: Observer
 * Interface for event listeners subscribing to the EventBus.
 */
public interface EventListener {
    void onEvent(SystemEvent event);
}
