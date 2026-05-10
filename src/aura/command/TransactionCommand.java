package aura.command;

import aura.domain.TransactionResult;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. COMMAND (Behavioural)
 *    - Role      : Command interface
 *    - Intent    : Encapsulates a request (transaction operation) as an
 *                  object, allowing requests to be parameterised,
 *                  queued, and undone. Every transaction operation
 *                  (purchase, refund, restock) implements this interface.
 *    - Concrete commands: PurchaseCommand (with Memento rollback),
 *                  RefundCommand, RestockCommand.
 *    - Invoker   : KioskFacade calls command.execute() without knowing
 *                  which concrete command it holds.
 *    - Why here  : Decouples the invoker (Facade/UI) from the concrete
 *                  logic of each transaction type, enabling easy
 *                  addition of new transaction kinds without touching
 *                  the facade.
 * ============================================================
 */
// Design Pattern: Command (interface)
public interface TransactionCommand {
    TransactionResult execute();
}
