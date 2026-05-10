package aura.command;

import aura.domain.Product;
import aura.domain.TransactionResult;
import aura.domain.TransactionStatus;
import aura.inventory.InventoryManager;

// Design Pattern: Command
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
