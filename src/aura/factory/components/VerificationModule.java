package aura.factory.components;

import aura.domain.Product;

public interface VerificationModule {
    String name();

    boolean verify(String userId, Product product);
}
