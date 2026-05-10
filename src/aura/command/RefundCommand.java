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
 *    - Role      : Concrete Command — "Refund" operation
 *    - Intent    : Encapsulates a stock-restoration refund as a
 *                  self-contained executable object. Calling execute()
 *                  returns the quantity of a product to inventory and
 *                  returns a REFUNDED TransactionResult.
 *    - Invoker   : KioskFacade.refundTransaction() — the Facade creates
 *                  and executes this command without needing to know
 *                  the refund logic directly.
 * ============================================================
 */
// Design Pattern: Command (Concrete Command — Refund)
public class RefundCommand implements TransactionCommand {
    private final InventoryManager inventory;
    private final Product product;
    private final int quantity;

    public RefundCommand(InventoryManager inventory, Product product, int quantity) {
        this.inventory = inventory;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public TransactionResult execute() {
        inventory.refund(product, quantity);
        return TransactionResult.success(TransactionStatus.REFUNDED, 0.0,
                "stock restored for refund", product, quantity);
    }
}
