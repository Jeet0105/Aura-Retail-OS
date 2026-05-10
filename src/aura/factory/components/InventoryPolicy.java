package aura.factory.components;

import aura.domain.Product;
import aura.state.KioskState;

public interface InventoryPolicy {
    String name();

    boolean canPurchase(Product product, int quantity, int availableStock, KioskState state);

    String denialReason(Product product, int quantity, int availableStock, KioskState state);
}
