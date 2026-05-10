package aura.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
