package aura.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Design Pattern: Observer
public class EventBus {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, List<EventListener>> listeners = new LinkedHashMap<>();

    public void subscribe(String eventType, EventListener listener) {
        lock.writeLock().lock();
        try {
            listeners.computeIfAbsent(eventType, ignored -> new ArrayList<>()).add(listener);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void publish(SystemEvent event) {
        dispatch(event);
    }

    public void publishBatch(List<SystemEvent> events) {
        List<SystemEvent> ordered = new ArrayList<>(events);
        Collections.sort(ordered, Comparator.comparingInt(e -> e.priority().rank()));
        for (SystemEvent event : ordered) {
            dispatch(event);
        }
    }

    private void dispatch(SystemEvent event) {
        List<EventListener> snapshot;
        lock.readLock().lock();
        try {
            snapshot = new ArrayList<>(listeners.getOrDefault(event.type(), Collections.emptyList()));
        } finally {
            lock.readLock().unlock();
        }
        for (EventListener listener : snapshot) {
            listener.onEvent(event);
        }
    }
}
