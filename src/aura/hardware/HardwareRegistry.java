package aura.hardware;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HardwareRegistry {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, HardwareModule> modules = new LinkedHashMap<>();

    public void register(HardwareModule module) {
        lock.writeLock().lock();
        try {
            modules.put(module.getId(), module);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isOperational(String hardwareId) {
        if (hardwareId == null || hardwareId.isEmpty()) {
            return true;
        }
        lock.readLock().lock();
        try {
            HardwareModule module = modules.get(hardwareId);
            return module != null && module.isOperational();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void markFaulted(String hardwareId) {
        setStatus(hardwareId, false);
    }

    public void repair(String hardwareId) {
        setStatus(hardwareId, true);
    }

    public void setStatus(String hardwareId, boolean operational) {
        lock.writeLock().lock();
        try {
            HardwareModule module = modules.get(hardwareId);
            if (module != null) {
                module.setOperational(operational);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<HardwareModule> list() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(modules.values());
        } finally {
            lock.readLock().unlock();
        }
    }
}
