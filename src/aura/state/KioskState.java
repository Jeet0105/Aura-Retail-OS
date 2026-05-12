package aura.state;

/**
 * ============================================================
 * DESIGN PATTERNS USED IN THIS FILE
 * ============================================================
 *
 * 1. STATE (Behavioural)
 *    - Role      : State interface
 *    - Intent    : Defines the behaviour contract for all kiosk
 *                  operational modes. Each concrete state encapsulates
 *                  what the kiosk can and cannot do in that mode,
 *                  eliminating complex if/switch chains in KioskFacade.
 *    - Concrete states: ActiveMode, PowerSavingMode, MaintenanceMode,
 *                  EmergencyLockdownMode.
 *    - Context   : KioskContext holds the current state and delegates
 *                  calls to it.
 *    - Why here  : Adding a new operational mode requires only a new
 *                  implementing class — no changes to KioskFacade,
 *                  InventoryPolicy, or any other client (Open/Closed).
 * ============================================================
 */
// Design Pattern: State (interface)
public interface KioskState {
    String name();

    boolean canPurchase();

    boolean canRestock();

    String operationalNote();

    boolean delayedHardware();
}
