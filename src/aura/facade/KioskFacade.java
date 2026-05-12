package aura.facade;

import aura.command.PurchaseCommand;
import aura.command.RefundCommand;
import aura.command.RestockCommand;
import aura.domain.KioskType;
import aura.domain.Product;
import aura.domain.ProductCategory;
import aura.domain.TransactionResult;
import aura.events.EventBus;
import aura.events.HardwareFailureEvent;
import aura.events.LowStockEvent;
import aura.factory.KioskFactory;
import aura.factory.components.Dispenser;
import aura.factory.components.InventoryPolicy;
import aura.factory.components.VerificationModule;
import aura.failure.FailureHandler;
import aura.failure.FailureReport;
import aura.hardware.HardwareModule;
import aura.hardware.HardwareRegistry;
import aura.inventory.InventoryManager;
import aura.pricing.PricingStrategy;
import aura.state.ActiveMode;
import aura.state.KioskContext;
import aura.state.KioskState;
import java.util.List;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. FACADE (Structural)
 *    - Role      : Facade
 *    - Intent    : Provides a unified, high-level interface (purchaseItem,
 *                  refundTransaction, restockInventory, diagnostics) to
 *                  the complex underlying subsystems (Inventory, Hardware,
 *                  EventBus, FailureChain, Pricing, Commands).
 *    - Why here  : Simplifies the client (AuraConsoleApp) by shielding
 *                  it from the intricate coordination required to
 *                  execute a transaction or gather system health.
 *
 * 2. INTEGRATION POINT
 *    - This class acts as the central hub where almost all other
 *      patterns converge:
 *        * Abstract Factory (provides components during construction)
 *        * State (KioskContext gates operations)
 *        * Strategy (PricingStrategy calculates final prices)
 *        * Command & Memento (PurchaseCommand encapsulates the work)
 *        * Observer (EventBus publishes low stock / failure events)
 *        * Chain of Responsibility (FailureHandler triggered on errors)
 * ============================================================
 */
// Design Pattern: Facade
public class KioskFacade {
    private final KioskFactory factory;
    private final InventoryManager inventory;
    private final HardwareRegistry hardware;
    private final EventBus eventBus;
    private final FailureHandler failureChain;
    private final KioskContext stateContext = new KioskContext();
    private PricingStrategy pricing;
    private final Dispenser dispenser;
    private final VerificationModule verification;
    private InventoryPolicy policy;

    public KioskFacade(KioskFactory factory, InventoryManager inventory, HardwareRegistry hardware,
                       EventBus eventBus, FailureHandler failureChain) {
        this.factory = factory;
        this.inventory = inventory;
        this.hardware = hardware;
        this.eventBus = eventBus;
        this.failureChain = failureChain;
        this.pricing = factory.createDefaultPricing();
        this.dispenser = factory.createDispenser();
        this.verification = factory.createVerificationModule();
        this.policy = factory.createInventoryPolicy();
    }

    public KioskType type() {
        return factory.type();
    }

    public PricingStrategy pricing() {
        return pricing;
    }

    public KioskState state() {
        return stateContext.state();
    }

    public void setPricing(PricingStrategy pricing) {
        this.pricing = pricing;
    }

    public void setState(KioskState state) {
        stateContext.transitionTo(state);
    }

    public void setInventoryPolicy(InventoryPolicy policy) {
        this.policy = policy;
    }

    /**
     * Resets only the inventory policy to this kiosk family's factory default,
     * without changing operational state or pricing.
     */
    public void resetInventoryPolicyToFactoryDefault() {
        this.policy = factory.createInventoryPolicy();
    }

    public void restoreFactoryOperationalDefaults() {
        stateContext.transitionTo(new ActiveMode());
        this.pricing = factory.createDefaultPricing();
        this.policy = factory.createInventoryPolicy();
    }

    public TransactionResult purchaseItem(String userId, Product product, int quantity,
                                          boolean delayedHardware, boolean forceFailure) {
        boolean effectiveDelayed = delayedHardware || stateContext.state().delayedHardware();
        PurchaseCommand command = new PurchaseCommand(inventory, hardware, dispenser, verification, policy,
                pricing, stateContext.state(), userId, product, quantity, effectiveDelayed, forceFailure);
        TransactionResult result = command.execute();
        if (result.isSuccess()) {
            int available = inventory.availableStock(product, hardware);
            if (available <= 5) {
                eventBus.publish(new LowStockEvent(product.getName(), available, 5));
            }
        } else if (result.getStatus().name().equals("ROLLED_BACK")) {
            eventBus.publish(new HardwareFailureEvent(dispenser.name(), result.getMessage()));
            failureChain.handle(new FailureReport(dispenser.name(), result.getMessage()));
        }
        return result;
    }

    public TransactionResult refundTransaction(Product product, int quantity) {
        return new RefundCommand(inventory, product, quantity).execute();
    }

    public TransactionResult restockInventory(Product product, int quantity) {
        if (!stateContext.state().canRestock()) {
            return TransactionResult.failure(aura.domain.TransactionStatus.DECLINED, 0.0,
                    "current state blocks restocking", product, quantity);
        }
        return new RestockCommand(inventory, product, quantity).execute();
    }

    public String diagnostics() {
        boolean allHardwareOk = true;
        StringBuilder down = new StringBuilder();
        List<HardwareModule> modules = hardware.list();
        for (HardwareModule module : modules) {
            if (!module.isOperational()) {
                allHardwareOk = false;
                if (down.length() > 0) {
                    down.append(", ");
                }
                down.append(module.getName());
            }
        }
        boolean networkOk = hardware.isOperational("NET-1");
        boolean operational = allHardwareOk && networkOk && stateContext.state().canPurchase();
        return "Kiosk: " + type().label()
                + "\nState: " + stateContext.state().name() + " - " + stateContext.state().operationalNote()
                + "\nPricing: " + pricing.name()
                + "\nVerification: " + verification.name()
                + "\nInventory Policy: " + policy.name()
                + "\nHardware: " + (allHardwareOk ? "all modules operational" : "faulted: " + down)
                + "\nNetwork: " + (networkOk ? "connected" : "offline")
                + "\nDerived Status: " + (operational ? "FULLY OPERATIONAL" : "DEGRADED / LIMITED");
    }

    public boolean accepts(Product product) {
        ProductCategory category = product.getCategory();
        if (type() == KioskType.PHARMACY) {
            return category == ProductCategory.PHARMACY;
        }
        if (type() == KioskType.FOOD) {
            return category == ProductCategory.FOOD;
        }
        return category == ProductCategory.EMERGENCY;
    }
}
