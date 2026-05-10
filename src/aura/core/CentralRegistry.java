package aura.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. SINGLETON (Creational)
 *    - Role      : Singleton (single shared instance)
 *    - Intent    : Guarantee that exactly one CentralRegistry instance
 *                  exists across the entire application, providing a
 *                  global access point to system-wide configuration.
 *    - How       : Double-checked locking with a volatile field and a
 *                  dedicated INSTANCE_LOCK object prevents race
 *                  conditions during lazy initialization.
 *    - Why here  : Configuration keys (e.g. "city", "emergency_mode")
 *                  must be readable and writable from multiple
 *                  subsystems (Facade, Persistence, UI) without
 *                  passing the object through every constructor.
 *    - Thread-safety: ReentrantReadWriteLock allows many concurrent
 *                  readers while serialising writers.
 * ============================================================
 */
// Design Pattern: Singleton
public final class CentralRegistry {
    private static volatile CentralRegistry instance;
    private static final Object INSTANCE_LOCK = new Object();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, String> config = new LinkedHashMap<>();

    private CentralRegistry() {
    }

    public static CentralRegistry getInstance() {
        if (instance == null) {
            synchronized (INSTANCE_LOCK) {
                if (instance == null) {
                    instance = new CentralRegistry();
                }
            }
        }
        return instance;
    }

    public void set(String key, String value) {
        lock.writeLock().lock();
        try {
            config.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String get(String key, String fallback) {
        lock.readLock().lock();
        try {
            return config.getOrDefault(key, fallback);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, String> snapshot() {
        lock.readLock().lock();
        try {
            return new LinkedHashMap<>(config);
        } finally {
            lock.readLock().unlock();
        }
    }
}
