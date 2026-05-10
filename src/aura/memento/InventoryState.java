package aura.memento;

import aura.domain.InventoryItem;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. MEMENTO (Behavioural)
 *    - Role      : Memento (the stored snapshot object)
 *    - Intent    : Captures a deep copy of InventoryManager's internal
 *                  stock map at a specific point in time without
 *                  exposing the manager's internal representation to
 *                  external objects.
 *    - Originator: InventoryManager (calls saveState() / restoreState())
 *    - Caretaker : PurchaseCommand (holds the InventoryState reference
 *                  between reserve() and confirmReservation(); discards
 *                  it on success or passes it back on failure).
 *    - Deep copy : Constructor deep-copies each InventoryItem via
 *                  item.copy() so the memento is completely independent
 *                  of subsequent mutations.
 *    - Why here  : Transaction atomicity — if dispensing fails mid-way,
 *                  PurchaseCommand calls InventoryManager.restoreState(before)
 *                  to undo any reserved-stock change.
 * ============================================================
 */
// Design Pattern: Memento (Memento object)
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
