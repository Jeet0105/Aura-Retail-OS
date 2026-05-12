package aura.factory.components;

import aura.domain.Product;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. ABSTRACT FACTORY (Creational)
 *    - Role      : Concrete Product — shared Dispenser implementation
 *    - Intent    : A single reusable dispenser that all three kiosk
 *                  factories instantiate with different names and
 *                  verb labels (e.g. "Securely dispensing",
 *                  "Serving", "Releasing ration unit").
 *    - Why here  : Avoids duplicating dispense logic in three separate
 *                  classes; behavioural differences are injected at
 *                  construction time via the factory.
 *    - Simulation : Supports delayedHardware (Thread.sleep) to model
 *                  PowerSavingMode's slow hardware response, and
 *                  forceFailure to trigger the Chain of Responsibility
 *                  error recovery scenario.
 * ============================================================
 */
// Design Pattern: Abstract Factory (Concrete Product — BaseDispenser)
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
                Thread.sleep(2000);
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
