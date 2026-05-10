package aura.inventory;

import aura.domain.InventoryItem;
import aura.domain.Product;
import aura.hardware.HardwareRegistry;
import aura.memento.InventoryState;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. MEMENTO (Behavioural) — Originator role
 *    - Role      : Originator — owns the state that is snapshotted
 *    - Intent    : saveState() creates and returns an InventoryState
 *                  (the Memento) containing a deep copy of all current
 *                  stock. restoreState() replaces the internal map with
 *                  the snapshot's copy, rolling back any changes.
 *    - Caretaker : PurchaseCommand saves the state before reserving
 *                  stock and restores it when a DispenseException occurs.
 *    - Thread-safety: ReentrantLock guards all reads and writes,
 *                  ensuring saveState/restoreState are atomic with
 *                  respect to concurrent purchase threads.
 * ============================================================
 */
// Design Pattern: Memento (Originator)
public class InventoryManager {
    private final ReentrantLock lock = new ReentrantLock();
    private Map<String, InventoryItem> items = new LinkedHashMap<>();

    public void putItem(Product product, int stock, int reserved) {
        lock.lock();
        try {
            items.put(product.getId(), new InventoryItem(product, stock, reserved));
        } finally {
            lock.unlock();
        }
    }

    public void restock(Product product, int amount) {
        lock.lock();
        try {
            InventoryItem item = items.get(product.getId());
            if (item == null) {
                items.put(product.getId(), new InventoryItem(product, amount, 0));
            } else {
                item.addStock(amount);
            }
        } finally {
            lock.unlock();
        }
    }

    public Optional<Product> findProduct(String productId) {
        lock.lock();
        try {
            InventoryItem item = items.get(productId);
            return item == null ? Optional.empty() : Optional.of(item.getProduct());
        } finally {
            lock.unlock();
        }
    }

    public boolean reserve(Product product, int quantity, HardwareRegistry hardware) {
        lock.lock();
        try {
            InventoryItem item = items.get(product.getId());
            if (item == null || !isHardwareAvailable(product, hardware)) {
                return false;
            }
            return item.reserve(quantity);
        } finally {
            lock.unlock();
        }
    }

    public void confirmReservation(Product product, int quantity) {
        lock.lock();
        try {
            InventoryItem item = items.get(product.getId());
            if (item != null) {
                item.confirmReservation(quantity);
            }
        } finally {
            lock.unlock();
        }
    }

    public void releaseReservation(Product product, int quantity) {
        lock.lock();
        try {
            InventoryItem item = items.get(product.getId());
            if (item != null) {
                item.releaseReservation(quantity);
            }
        } finally {
            lock.unlock();
        }
    }

    public void refund(Product product, int quantity) {
        lock.lock();
        try {
            InventoryItem item = items.get(product.getId());
            if (item == null) {
                items.put(product.getId(), new InventoryItem(product, quantity, 0));
            } else {
                item.refund(quantity);
            }
        } finally {
            lock.unlock();
        }
    }

    public int totalStock(String productId) {
        lock.lock();
        try {
            InventoryItem item = items.get(productId);
            return item == null ? 0 : item.getStock();
        } finally {
            lock.unlock();
        }
    }

    public int reservedStock(String productId) {
        lock.lock();
        try {
            InventoryItem item = items.get(productId);
            return item == null ? 0 : item.getReserved();
        } finally {
            lock.unlock();
        }
    }

    public int availableStock(Product product, HardwareRegistry hardware) {
        lock.lock();
        try {
            InventoryItem item = items.get(product.getId());
            if (item == null || !isHardwareAvailable(product, hardware)) {
                return 0;
            }
            return Math.max(0, item.getStock() - item.getReserved());
        } finally {
            lock.unlock();
        }
    }

    public List<InventoryItem> listItems() {
        lock.lock();
        try {
            List<InventoryItem> copy = new ArrayList<>();
            for (InventoryItem item : items.values()) {
                copy.add(item.copy());
            }
            return copy;
        } finally {
            lock.unlock();
        }
    }

    public InventoryState saveState() {
        lock.lock();
        try {
            return new InventoryState(items);
        } finally {
            lock.unlock();
        }
    }

    public void restoreState(InventoryState state) {
        lock.lock();
        try {
            items = state.copyItems();
        } finally {
            lock.unlock();
        }
    }

    private boolean isHardwareAvailable(Product product, HardwareRegistry hardware) {
        return !product.requiresHardware() || hardware.isOperational(product.getRequiredHardwareId());
    }
}
