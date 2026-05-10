package aura.factory.components;

import aura.domain.Product;

public class BaseDispenser implements Dispenser {
    private final String name;
    private final String dispenseVerb;

    public BaseDispenser(String name, String dispenseVerb) {
        this.name = name;
        this.dispenseVerb = dispenseVerb;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void dispense(Product product, int quantity, boolean delayed, boolean forceFailure) throws DispenseException {
        if (delayed) {
            try {
                Thread.sleep(450);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DispenseException("Delayed hardware response interrupted");
            }
        }
        if (forceFailure) {
            throw new DispenseException(name + " reported a forced motor failure");
        }
        for (int i = 0; i < quantity; i++) {
            System.out.println("    " + dispenseVerb + " " + product.getName());
        }
    }
}
