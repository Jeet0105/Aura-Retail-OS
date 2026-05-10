package aura.command;

import aura.domain.Product;
import aura.domain.TransactionResult;
import aura.domain.TransactionStatus;
import aura.inventory.InventoryManager;

// Design Pattern: Command
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
