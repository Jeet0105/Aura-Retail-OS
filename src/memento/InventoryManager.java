package memento;

/**
 * Design Pattern: Memento (Originator)
 * Manages inventory with thread-safe operations and memento support.
 * Tracks both total stock and available stock (considering reserved/unavailable items).
 * Supports hardware dependency constraint (4.2c) — products tied to specific hardware modules.
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryManager {
    private Map<String, Integer> inventory;
    private Map<String, Integer> reservedStock;      // Items reserved for pending transactions
    private Map<String, Integer> unavailableStock;   // Items unavailable due to hardware faults
    private Map<String, String> hardwareDependencies; // productId -> required hardware module
    private Set<String> faultedHardware;              // Set of currently faulted hardware modules
    private final ReentrantLock lock;

    public InventoryManager() {
        this.inventory = new HashMap<>();
        this.reservedStock = new HashMap<>();
        this.unavailableStock = new HashMap<>();
        this.hardwareDependencies = new HashMap<>();
        this.faultedHardware = new HashSet<>();
        this.lock = new ReentrantLock();
    }

    public void addItemStock(String productId, int amount) {
        lock.lock();
        try {
            inventory.put(productId, inventory.getOrDefault(productId, 0) + amount);
            // Initialize reserved and unavailable if not present
            reservedStock.putIfAbsent(productId, 0);
            unavailableStock.putIfAbsent(productId, 0);
        } finally {
            lock.unlock();
        }
    }

    public boolean reduceStock(String productId, int amount) {
        lock.lock();
        try {
            int current = inventory.getOrDefault(productId, 0);
            if (current >= amount) {
                inventory.put(productId, current - amount);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Reserves stock for a pending transaction (e.g., during hardware operation).
     * Reserved items are deducted from available stock but not from total inventory.
     */
    public boolean reserveStock(String productId, int amount) {
        lock.lock();
        try {
            int current = inventory.getOrDefault(productId, 0);
            int reserved = reservedStock.getOrDefault(productId, 0);
            int unavailable = unavailableStock.getOrDefault(productId, 0);
            int available = current - reserved - unavailable;
            
            if (available >= amount) {
                reservedStock.put(productId, reserved + amount);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Confirms a reservation, permanently reducing inventory.
     */
    public void confirmReservation(String productId, int amount) {
        lock.lock();
        try {
            int reserved = reservedStock.getOrDefault(productId, 0);
            reservedStock.put(productId, Math.max(0, reserved - amount));
            reduceStock(productId, amount);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Releases a reservation (e.g., on transaction failure).
     */
    public void releaseReservation(String productId, int amount) {
        lock.lock();
        try {
            int reserved = reservedStock.getOrDefault(productId, 0);
            reservedStock.put(productId, Math.max(0, reserved - amount));
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Marks stock as unavailable due to hardware fault.
     */
    public void markUnavailable(String productId, int amount) {
        lock.lock();
        try {
            int unavailable = unavailableStock.getOrDefault(productId, 0);
            unavailableStock.put(productId, unavailable + amount);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Restores previously unavailable stock after hardware recovery.
     */
    public void restoreAvailable(String productId, int amount) {
        lock.lock();
        try {
            int unavailable = unavailableStock.getOrDefault(productId, 0);
            unavailableStock.put(productId, Math.max(0, unavailable - amount));
        } finally {
            lock.unlock();
        }
    }
    
    public int getStock(String productId) {
        lock.lock();
        try {
            return inventory.getOrDefault(productId, 0);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Returns available stock considering reserved and unavailable items.
     * This is the derived available stock as per requirement 4.1a.
     */
    public int getAvailableStock(String productId) {
        lock.lock();
        try {
            int total = inventory.getOrDefault(productId, 0);
            int reserved = reservedStock.getOrDefault(productId, 0);
            int unavailable = unavailableStock.getOrDefault(productId, 0);
            return total - reserved - unavailable;
        } finally {
            lock.unlock();
        }
    }
    
    public int getReservedStock(String productId) {
        lock.lock();
        try {
            return reservedStock.getOrDefault(productId, 0);
        } finally {
            lock.unlock();
        }
    }
    
    public int getUnavailableStock(String productId) {
        lock.lock();
        try {
            return unavailableStock.getOrDefault(productId, 0);
        } finally {
            lock.unlock();
        }
    }

    public InventoryState saveState() {
        lock.lock();
        try {
            return new InventoryState(this.inventory);
        } finally {
            lock.unlock();
        }
    }

    public void restoreState(InventoryState state) {
        lock.lock();
        try {
            this.inventory = new HashMap<>(state.getStock());
            System.out.println("[InventoryManager] State restored to previous snapshot.");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Returns a snapshot of the current inventory for persistence.
     */
    public Map<String, Integer> getSnapshot() {
        lock.lock();
        try {
            return new HashMap<>(this.inventory);
        } finally {
            lock.unlock();
        }
    }
    
    // ========== Hardware Dependency Constraint (Section 4.2c) ==========
    
    /**
     * Associates a product with a required hardware module.
     * Products requiring specific hardware (e.g., refrigeration) become
     * unavailable if that hardware module is reported as faulted.
     */
    public void setHardwareDependency(String productId, String hardwareModule) {
        lock.lock();
        try {
            hardwareDependencies.put(productId, hardwareModule);
            System.out.println("[InventoryManager] Product '" + productId + "' depends on hardware: " + hardwareModule);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Reports a hardware module as faulted.
     * All products depending on this hardware become unavailable.
     */
    public void reportHardwareFault(String hardwareModule) {
        lock.lock();
        try {
            faultedHardware.add(hardwareModule);
            System.out.println("[InventoryManager] Hardware fault reported: " + hardwareModule);
            // Log affected products
            for (Map.Entry<String, String> entry : hardwareDependencies.entrySet()) {
                if (entry.getValue().equals(hardwareModule)) {
                    System.out.println("[InventoryManager] -> Product '" + entry.getKey() + "' is now UNAVAILABLE");
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Clears a hardware fault, restoring availability of dependent products.
     */
    public void clearHardwareFault(String hardwareModule) {
        lock.lock();
        try {
            faultedHardware.remove(hardwareModule);
            System.out.println("[InventoryManager] Hardware restored: " + hardwareModule);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Checks if a product is available considering hardware dependencies.
     * Returns false if the product depends on faulted hardware.
     */
    public boolean isProductAvailable(String productId) {
        lock.lock();
        try {
            String requiredHardware = hardwareDependencies.get(productId);
            if (requiredHardware != null && faultedHardware.contains(requiredHardware)) {
                return false;
            }
            return true;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Returns the set of currently faulted hardware modules.
     */
    public Set<String> getFaultedHardware() {
        lock.lock();
        try {
            return new HashSet<>(faultedHardware);
        } finally {
            lock.unlock();
        }
    }
}
