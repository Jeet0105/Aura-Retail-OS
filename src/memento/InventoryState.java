package memento;

/**
 * Design Pattern: Memento (Memento)
 * Immutable snapshot of inventory state for rollback capability.
 */

import java.util.HashMap;
import java.util.Map;

public class InventoryState {
    private final Map<String, Integer> stock;

    public InventoryState(Map<String, Integer> currentStock) {
        this.stock = new HashMap<>(currentStock); // Deep copy
    }

    public Map<String, Integer> getStock() {
        return stock;
    }
}
