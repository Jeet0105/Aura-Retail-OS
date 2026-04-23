package core;

import core.events.SystemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Design Pattern: Observer (Publish-Subscribe)
 * Central event bus for decoupled communication between system components.
 * Supports event priority levels - EMERGENCY_MODE events are processed first.
 */
public class EventBus {
    private static EventBus instance;
    private Map<String, List<EventListener>> listeners;
    private final ReentrantLock lock;
    
    // Event priority levels
    public enum Priority { LOW, NORMAL, HIGH, CRITICAL }
    
    // Priority mapping for event types
    private static final Map<String, Priority> EVENT_PRIORITIES = new HashMap<>();
    static {
        EVENT_PRIORITIES.put("EMERGENCY_MODE", Priority.CRITICAL);
        EVENT_PRIORITIES.put("HARDWARE_FAILURE", Priority.HIGH);
        EVENT_PRIORITIES.put("LOW_STOCK", Priority.NORMAL);
    }

    private EventBus() {
        listeners = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void subscribe(String eventType, EventListener listener) {
        lock.lock();
        try {
            listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        } finally {
            lock.unlock();
        }
    }

    public void publish(SystemEvent event) {
        String eventType = event.getEventType();
        Priority priority = EVENT_PRIORITIES.getOrDefault(eventType, Priority.NORMAL);
        System.out.println("[EventBus] Publishing " + priority + " priority event: " + eventType);
        
        // Copy listener list under lock, then iterate outside the lock
        List<EventListener> eventListeners;
        lock.lock();
        try {
            eventListeners = new ArrayList<>(listeners.getOrDefault(eventType, new ArrayList<>()));
        } finally {
            lock.unlock();
        }
        
        // Invoke listeners without holding the lock
        for (EventListener listener : eventListeners) {
            listener.onEvent(event);
        }
    }
    
    /**
     * Publishes an event with immediate priority processing.
     * Critical priority events interrupt normal operations.
     */
    public void publishImmediate(SystemEvent event) {
        String eventType = event.getEventType();
        System.out.println("[EventBus] IMMEDIATE PUBLISH - CRITICAL: " + eventType);
        
        List<EventListener> eventListeners;
        lock.lock();
        try {
            eventListeners = new ArrayList<>(listeners.getOrDefault(eventType, new ArrayList<>()));
        } finally {
            lock.unlock();
        }
        
        for (EventListener listener : eventListeners) {
            listener.onEvent(event);
        }
    }
}
