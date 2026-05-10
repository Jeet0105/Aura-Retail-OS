package aura.factory.components;

import aura.domain.Product;

public interface Dispenser {
    String name();

    void dispense(Product product, int quantity, boolean delayed, boolean forceFailure) throws DispenseException;
}
