package aura.command;

import aura.domain.Product;
import aura.domain.TransactionResult;
import aura.domain.TransactionStatus;
import aura.inventory.InventoryManager;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. COMMAND (Behavioural)
 *    - Role      : Concrete Command — "Restock" operation
 *    - Intent    : Encapsulates a restocking operation as a
 *                  self-contained executable object. Calling execute()
 *                  adds the specified quantity to inventory and returns
 *                  a RESTOCKED TransactionResult.
 *    - Invoker   : KioskFacade.restockInventory() — guards with a
 *                  state check first, then delegates to this command.
 * ============================================================
 */
// Design Pattern: Command (Concrete Command — Restock)
public class RestockCommand implements TransactionCommand {
    private final InventoryManager inventory;
    private final Product product;
    private final int quantity;

    public RestockCommand(InventoryManager inventory, Product product, int quantity) {
        this.inventory = inventory;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public TransactionResult execute() {
        inventory.restock(product, quantity);
        return TransactionResult.success(TransactionStatus.RESTOCKED, 0.0,
                "inventory replenished", product, quantity);
    }
}
