package factory.components;

/**
 * Design Pattern: Abstract Factory (Product)
 * Abstract base class for user/product verification modules.
 */

public interface VerificationModule {
    boolean verify(String userId, String productId);
}
