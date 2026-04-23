package factory.components;

/**
 * Design Pattern: Abstract Factory (Product)
 * Abstract base class for inventory management policies.
 */

public interface InventoryPolicy {
    boolean canPurchase(String productId, int requestedAmount, int currentStock);
}
