package aura.memento;

import aura.domain.InventoryItem;
import java.util.LinkedHashMap;
import java.util.Map;

// Design Pattern: Memento
public class InventoryState {
    private final Map<String, InventoryItem> items;

    public InventoryState(Map<String, InventoryItem> items) {
        this.items = new LinkedHashMap<>();
        for (Map.Entry<String, InventoryItem> entry : items.entrySet()) {
            this.items.put(entry.getKey(), entry.getValue().copy());
        }
    }

    public Map<String, InventoryItem> copyItems() {
        Map<String, InventoryItem> copy = new LinkedHashMap<>();
        for (Map.Entry<String, InventoryItem> entry : items.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return copy;
    }
}
