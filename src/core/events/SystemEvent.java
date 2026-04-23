package core.events;

/**
 * Design Pattern: Observer (Event)
 * Base interface for all system events published via EventBus.
 */
public interface SystemEvent {
    String getEventType();
}
