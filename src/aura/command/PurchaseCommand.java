package aura.command;

import aura.domain.Product;
import aura.domain.TransactionResult;
import aura.domain.TransactionStatus;
import aura.factory.components.DispenseException;
import aura.factory.components.Dispenser;
import aura.factory.components.InventoryPolicy;
import aura.factory.components.VerificationModule;
import aura.hardware.HardwareRegistry;
import aura.inventory.InventoryManager;
import aura.memento.InventoryState;
import aura.pricing.PricingStrategy;
import aura.state.KioskState;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. COMMAND (Behavioural)
 *    - Role      : Concrete Command — "Purchase" operation
 *    - Intent    : Encapsulates a full purchase transaction (verify user,
 *                  check policy, reserve stock, dispense item) as a
 *                  single executable object. KioskFacade creates and
 *                  invokes it without knowing the details.
 *    - Parameters: All required collaborators (inventory, hardware,
 *                  dispenser, verification, policy, pricing, state,
 *                  userId, product, quantity, flags) are injected via
 *                  the constructor — the classic Command parameterisation.
 *
 * 2. MEMENTO (Behavioural)
 *    - Role      : Originator (uses InventoryManager as Originator,
 *                  InventoryState as Memento)
 *    - Intent    : Before modifying inventory (reserve), a memento of
 *                  the current InventoryState is saved. If the dispenser
 *                  throws DispenseException, the memento is restored to
 *                  roll back all stock changes atomically — no partial
 *                  state can be observed by other threads.
 *    - Integration: Command + Memento work together here to implement
 *                  atomic, undoable transactions.
 * ============================================================
 */
// Design Pattern: Command (Concrete Command) + Memento (rollback)
public class PurchaseCommand implements TransactionCommand {
    private final InventoryManager inventory;
    private final HardwareRegistry hardware;
    private final Dispenser dispenser;
    private final VerificationModule verification;
    private final InventoryPolicy policy;
    private final PricingStrategy pricing;
    private final KioskState state;
    private final String userId;
    private final Product product;
    private final int quantity;
    private final boolean delayedHardware;
    private final boolean forceFailure;

    public PurchaseCommand(InventoryManager inventory, HardwareRegistry hardware, Dispenser dispenser,
                           VerificationModule verification, InventoryPolicy policy, PricingStrategy pricing,
                           KioskState state, String userId, Product product, int quantity,
                           boolean delayedHardware, boolean forceFailure) {
        this.inventory = inventory;
        this.hardware = hardware;
        this.dispenser = dispenser;
        this.verification = verification;
        this.policy = policy;
        this.pricing = pricing;
        this.state = state;
        this.userId = userId;
        this.product = product;
        this.quantity = quantity;
        this.delayedHardware = delayedHardware;
        this.forceFailure = forceFailure;
    }

    @Override
    public TransactionResult execute() {
        double finalPrice = pricing.price(product, quantity);
        int available = inventory.availableStock(product, hardware);

        if (!verification.verify(userId, product)) {
            return TransactionResult.failure(TransactionStatus.DECLINED, finalPrice,
                    verification.name() + " rejected the request", product, quantity);
        }
        if (!policy.canPurchase(product, quantity, available, state)) {
            return TransactionResult.failure(TransactionStatus.DECLINED, finalPrice,
                    policy.denialReason(product, quantity, available, state), product, quantity);
        }

        InventoryState before = inventory.saveState();
        if (!inventory.reserve(product, quantity, hardware)) {
            return TransactionResult.failure(TransactionStatus.DECLINED, finalPrice,
                    "stock could not be reserved atomically", product, quantity);
        }

        try {
            dispenser.dispense(product, quantity, delayedHardware, forceFailure);
            inventory.confirmReservation(product, quantity);
            return TransactionResult.success(TransactionStatus.SUCCESS, finalPrice,
                    "dispensed by " + dispenser.name(), product, quantity);
        } catch (DispenseException e) {
            inventory.restoreState(before);
            return TransactionResult.failure(TransactionStatus.ROLLED_BACK, finalPrice,
                    e.getMessage() + "; inventory restored from memento", product, quantity);
        }
    }
}
