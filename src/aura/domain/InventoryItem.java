package aura.domain;

public class InventoryItem {
    private final Product product;
    private int stock;
    private int reserved;

    public InventoryItem(Product product, int stock, int reserved) {
        this.product = product;
        this.stock = Math.max(0, stock);
        this.reserved = Math.max(0, reserved);
    }

    public Product getProduct() {
        return product;
    }

    public int getStock() {
        return stock;
    }

    public int getReserved() {
        return reserved;
    }

    public void addStock(int amount) {
        stock += Math.max(0, amount);
    }

    public boolean reserve(int amount) {
        if (amount <= 0 || stock - reserved < amount) {
            return false;
        }
        reserved += amount;
        return true;
    }

    public void confirmReservation(int amount) {
        int confirmed = Math.min(amount, reserved);
        reserved -= confirmed;
        stock -= confirmed;
    }

    public void releaseReservation(int amount) {
        reserved = Math.max(0, reserved - amount);
    }

    public void refund(int amount) {
        stock += Math.max(0, amount);
    }

    public InventoryItem copy() {
        return new InventoryItem(product, stock, reserved);
    }
}
