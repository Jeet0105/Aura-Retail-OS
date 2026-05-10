package aura.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. OBSERVER (Behavioural)
 *    - Role      : Subject / Event Bus (publisher side)
 *    - Intent    : Define a one-to-many dependency so that when one
 *                  object (e.g. KioskFacade) publishes an event, all
 *                  registered EventListeners are notified automatically.
 *    - How       : Listeners register by event-type string via
 *                  subscribe(). publish() / publishBatch() dispatch
 *                  events to a snapshot of the listener list so that
 *                  subscribe/unsubscribe during dispatch is safe.
 *    - Why here  : Decouples event producers (Facade, UI) from consumers
 *                  (stock alerts, hardware monitors, emergency handler)
 *                  without any direct reference between them.
 *    - Priority  : publishBatch() sorts events by EventPriority.rank()
 *                  so EMERGENCY events are always dispatched before
 *                  NORMAL ones regardless of insertion order.
 *    - Thread-safety: ReentrantReadWriteLock guards the listeners map;
 *                  dispatch works on a copied snapshot to prevent
 *                  ConcurrentModificationException.
 * ============================================================
 */
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
