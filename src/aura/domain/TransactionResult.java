package aura.domain;

public class TransactionResult {
    private final boolean success;
    private final TransactionStatus status;
    private final double finalPrice;
    private final String message;
    private final Product product;
    private final int quantity;

    public TransactionResult(boolean success, TransactionStatus status, double finalPrice,
                             String message, Product product, int quantity) {
        this.success = success;
        this.status = status;
        this.finalPrice = finalPrice;
        this.message = message;
        this.product = product;
        this.quantity = quantity;
    }

    public static TransactionResult success(TransactionStatus status, double finalPrice,
                                            String message, Product product, int quantity) {
        return new TransactionResult(true, status, finalPrice, message, product, quantity);
    }

    public static TransactionResult failure(TransactionStatus status, double finalPrice,
                                            String message, Product product, int quantity) {
        return new TransactionResult(false, status, finalPrice, message, product, quantity);
    }

    public boolean isSuccess() {
        return success;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public String getMessage() {
        return message;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
