# Aura Retail OS (Path A) — Detailed Project Report

## 1) Project overview

**Aura Retail OS** is an object-oriented simulation of a modular smart-city retail kiosk platform for the city of **Zephyrus**, built to replace a monolithic “Aura-Kiosk v1.0” with a design that can adapt to:

- Different kiosk environments (pharmacy, food/essentials, emergency relief)
- Changing policies (rationing, maintenance restrictions, emergency operations)
- Hardware failures and delayed responses
- Concurrent purchase requests without overselling inventory

This implementation follows **Path A: Adaptive Autonomous System** from `IT620_Project.pdf`. The focus is runtime adaptability (modes + pricing), robust transaction behavior (atomicity + rollback), event-driven subsystem communication, and staged failure recovery.

## 2) What is implemented (high-level)

At runtime, the console UI (`src/aura/ui/AuraConsoleApp.java`) bootstraps a kiosk ecosystem with:

- **Multiple kiosk families** created via an Abstract Factory (`src/aura/factory/`)
- A **single simplified interface** into the kiosk subsystems via a Facade (`src/aura/facade/KioskFacade.java`)
- An **inventory manager** with derived availability and transactional rollback (`src/aura/inventory/InventoryManager.java`, `src/aura/memento/InventoryState.java`)
- A **hardware registry** used both for availability constraints and for failure simulation (`src/aura/hardware/`)
- A **dynamic pricing system** (Strategy) (`src/aura/pricing/`)
- **Operational modes** (State) (`src/aura/state/`)
- **Command-modeled transactions** (`src/aura/command/`)
- An **event bus** (Observer) with **event priority** for emergency overrides (`src/aura/events/`)
- A **failure handling chain** (Chain of Responsibility) for staged recovery actions (`src/aura/failure/`)
- **Persistence** to CSV / config text files (`src/aura/persistence/PersistenceManager.java`)

## 3) Repository entry point and runtime flow

- Program entry point: `src/Main.java` → starts `AuraConsoleApp`
- Main simulation loop: `src/aura/ui/AuraConsoleApp.java`

### Startup sequence (what happens on launch)

1. **Load global configuration** into `CentralRegistry` from `data/config.txt` (or initialize defaults).
2. **Register hardware modules** via seed data (`SeedData.hardware(...)`).
3. **Load inventory** from `data/inventory.csv`; if missing/invalid, seed a default inventory and persist it.
4. **Register event subscribers** (low stock, hardware failure, emergency mode).
5. **Create kiosk families** (Pharmacy, Food, Emergency Relief) using factories and wrap each in a `KioskFacade`.
6. Start in the default active kiosk (Pharmacy) and display the dashboard menu.

## 4) Subsystems and design patterns (as implemented)

This section explains each major subsystem and how it maps to the project requirements.

### 4.1 Central Registry (Singleton)

- **File**: `src/aura/core/CentralRegistry.java`
- **Role**: A single shared store of global configuration and system status.
- **Examples of stored keys**:
  - `city` (default `"Zephyrus"`)
  - `emergency_mode` (`"true"`/`"false"`)
- **Thread-safety**: `ReentrantReadWriteLock` allows concurrent reads while serializing writes.

Why it matters (from requirements): provides a centralized source of truth for system-wide configuration without passing configuration objects through every constructor.

### 4.2 Kiosk creation (Abstract Factory)

- **Interface**: `src/aura/factory/KioskFactory.java`
- **Concrete families**:
  - `src/aura/factory/PharmacyKioskFactory.java`
  - `src/aura/factory/FoodKioskFactory.java`
  - `src/aura/factory/EmergencyReliefKioskFactory.java`
- **Factory-created components** (a compatible “family”):
  - Dispenser (`src/aura/factory/components/Dispenser.java`, `BaseDispenser`)
  - Verification module (`src/aura/factory/components/VerificationModule.java` plus per-factory implementations)
  - Inventory policy (`src/aura/factory/components/InventoryPolicy.java`)
  - Default pricing strategy (`src/aura/pricing/PricingStrategy.java`)

What this achieves: adding a new kiosk type is primarily “add a new factory” rather than editing the entire system, keeping coupling low.

### 4.3 KioskInterface layer (Facade)

The assignment expects an interface exposing operations like `purchaseItem()`, `refundTransaction()`, `runDiagnostics()`, `restockInventory()`.

- **Facade**: `src/aura/facade/KioskFacade.java`
- **Exposed operations**:
  - `purchaseItem(...)`
  - `refundTransaction(...)`
  - `restockInventory(...)`
  - `diagnostics()`

What the facade hides: the coordination across inventory reservation, verification, policy checks, hardware dispense, rollback, event publication, and failure-chain escalation.

### 4.4 Inventory system + derived attributes + hardware constraints

- **Inventory manager**: `src/aura/inventory/InventoryManager.java`
- **Inventory item model**: `src/aura/domain/InventoryItem.java`

#### Derived attribute: available stock

Available stock is computed dynamically as:

\[
\text{available} = \max(0, \text{stock} - \text{reserved})
\]

and also depends on hardware availability:

- If a product requires hardware (e.g., refrigeration) and that module is faulted/offline, **available becomes 0**.
- Implemented in `InventoryManager.availableStock(product, hardware)`.

This directly matches the requirement that “available stock” be derived from inventory + reservations + hardware faults, and that purchases be blocked when availability hits 0.

#### Hardware dependency constraint

Products can declare required hardware via `requiredHardwareId` (see `src/aura/domain/Product.java` and seeded examples in `src/aura/bootstrap/SeedData.java`).

Inventory reservation checks hardware via:

- `InventoryManager.reserve(product, qty, hardware)`
- `InventoryManager.isHardwareAvailable(product, hardware)`

### 4.5 Hardware abstraction layer (as implemented)

The project models hardware through a registry and module objects. This is used for:

- **Derived availability**: a product that depends on a faulted module is treated as unavailable.
- **Diagnostics**: the kiosk reports which modules are down and whether the network is online.
- **Failure simulation**: operator can mark a module as faulted, and the purchase flow can force a dispenser failure to trigger rollback + recovery.

Key files:

- `src/aura/hardware/HardwareRegistry.java`: thread-safe register/list and `isOperational(...)` checks.
- `src/aura/hardware/HardwareModule.java`: module identity + operational status.
- `src/aura/bootstrap/SeedData.java`: seeds modules like `REF-1` (refrigeration) and `NET-1` (network uplink).

### 4.6 Dynamic pricing system (Strategy)

Pricing is a runtime-swappable Strategy:

- **Strategy interface**: `src/aura/pricing/PricingStrategy.java`
- **Concrete strategies**:
  - `src/aura/pricing/StandardPricing.java` (“Standard”)
  - `src/aura/pricing/DiscountedPricing.java` (“Discount X%”)
  - `src/aura/pricing/EmergencyPricing.java` (“Emergency Relief”, 50% subsidy)

How switching works:

- Each kiosk starts with a **default strategy** chosen by its factory (e.g., Food starts discounted, Emergency starts emergency relief).
- The console can switch pricing at runtime via `KioskFacade.setPricing(...)`.
- On **emergency priority events**, all kiosks are switched to `EmergencyPricing` to reflect emergency policy changes.

This satisfies the Path A requirement for a dynamic pricing system with runtime switching.

### 4.7 Kiosk operational modes (State)

Operational modes are implemented using the State pattern:

- **State interface**: `src/aura/state/KioskState.java`
- **Context**: `src/aura/state/KioskContext.java`
- **Concrete modes**:
  - `src/aura/state/ActiveMode.java`: purchase ✅, restock ✅
  - `src/aura/state/PowerSavingMode.java`: purchase ✅, restock ❌ (and paired with delayed hardware simulation)
  - `src/aura/state/MaintenanceMode.java`: purchase ❌, restock ✅
  - `src/aura/state/EmergencyLockdownMode.java`: purchase ✅, restock ✅ (“emergency priority is active”)

Where modes affect behavior:

- Purchase eligibility is gated by `KioskState.canPurchase()` through the inventory policy (`BasicInventoryPolicy` delegates to state).
- Restocking is blocked in `KioskFacade.restockInventory(...)` if `state.canRestock()` is false.

### 4.8 Transaction system (Command)

All transactional operations are modeled as Commands:

- **Command interface**: `src/aura/command/TransactionCommand.java`
- **Concrete commands**:
  - `src/aura/command/PurchaseCommand.java`
  - `src/aura/command/RefundCommand.java`
  - `src/aura/command/RestockCommand.java`

The invoker is the `KioskFacade`, which creates the relevant command and calls `execute()` without embedding transaction logic inside the UI.

### 4.9 Atomic transactions + rollback (Command + Memento)

The purchase flow is designed to be **atomic**: it must either fully succeed or fully fail and restore state.

Implementation approach:

- Before reserving stock, `PurchaseCommand` captures a snapshot of inventory via `InventoryManager.saveState()`.
- It then reserves stock atomically.
- If dispensing completes, reservation is confirmed (stock is decremented and reserved reduced).
- If dispensing fails (throws `DispenseException`), `PurchaseCommand` restores inventory from the saved memento.

Key files:

- `src/aura/command/PurchaseCommand.java` (Command execution + rollback on failure)
- `src/aura/inventory/InventoryManager.java` (`saveState()`, `restoreState(...)`, reservation/confirmation)
- `src/aura/memento/InventoryState.java` (deep-copied memento snapshot)
- `src/aura/factory/components/BaseDispenser.java` (can simulate delayed hardware or forced failure)

This directly satisfies the Path A “Transaction Rollback” and “Atomic Transactions” constraints.

### 4.10 Event notification system + priority dispatch (Observer/EventBus)

Subsystems communicate through an EventBus rather than direct dependencies:

- **Event bus**: `src/aura/events/EventBus.java`
- **Listener**: `src/aura/events/EventListener.java`
- **Event interface/base**: `src/aura/events/SystemEvent.java`, `src/aura/events/AbstractSystemEvent.java`
- **Concrete events**:
  - `src/aura/events/LowStockEvent.java` (`EventPriority.NORMAL`)
  - `src/aura/events/HardwareFailureEvent.java` (`EventPriority.HIGH`)
  - `src/aura/events/EmergencyModeEvent.java` (`EventPriority.EMERGENCY`)
- **Priority ordering**: `src/aura/events/EventPriority.java`

Priority behavior:

- `EventBus.publishBatch(...)` sorts by `EventPriority.rank()`, so **emergency events dispatch first**, overriding normal operations immediately.

Example subscriber behavior (in the console app):

- On `EMERGENCY_MODE`, the app:
  - sets `CentralRegistry.emergency_mode = true`
  - sets every kiosk to `EmergencyLockdownMode`
  - switches every kiosk to `EmergencyPricing`

### 4.11 Failure handling system (Chain of Responsibility)

Hardware/transaction failures are handled by a staged chain:

- **Handler base**: `src/aura/failure/FailureHandler.java`
- **Concrete handlers**:
  - `src/aura/failure/RetryHandler.java` (retry loop simulation)
  - `src/aura/failure/RecalibrationHandler.java` (recalibration attempt simulation)
  - `src/aura/failure/TechnicianAlertHandler.java` (final escalation / ticket)

Where it is triggered:

- In `KioskFacade.purchaseItem(...)`, if the `PurchaseCommand` result is `ROLLED_BACK`, it publishes a `HardwareFailureEvent` and invokes the failure chain with a `FailureReport`.

This matches Path A’s “Failure Handling System” requirement (sequence of handlers).

### 4.12 System persistence (CSV + config file)

Persistence writes operational state to the `data/` directory:

- `data/inventory.csv`: products, stock, reserved, required hardware
- `data/transactions.csv`: append-only transaction history with a header
- `data/config.txt`: key-value registry snapshot (`key=value`)

Implementation:

- `src/aura/persistence/PersistenceManager.java` handles load/save for inventory, transactions, and config.
- `AuraConsoleApp` saves inventory and config on exit, and also saves inventory after purchase/restock/hardware actions to keep state current.

This satisfies the persistence requirement (inventory, transaction history, configuration).

## 5) How Path A requirements map to this codebase

This section maps each Path A requirement from `IT620_Project.pdf` to the concrete implementation locations.

- **Dynamic Pricing System (runtime switching)**:
  - Strategy interface + implementations: `src/aura/pricing/`
  - Runtime switching: `KioskFacade.setPricing(...)` and console menu option “Change pricing strategy”
  - Emergency override pricing: `AuraConsoleApp` emergency subscriber applies `EmergencyPricing` to all kiosks

- **Kiosk Operational Modes (Active/PowerSaving/Maintenance/Emergency)**:
  - State model: `src/aura/state/`
  - Runtime switching: `KioskFacade.setState(...)` via console “Change operational mode”

- **Failure Handling System (sequence of handlers)**:
  - Chain base + handlers: `src/aura/failure/`
  - Triggered on rolled-back purchases: `src/aura/facade/KioskFacade.java`

- **Transaction Rollback**:
  - Memento snapshot/restore: `InventoryManager.saveState()/restoreState(...)`, `InventoryState`
  - Used in purchase command: `PurchaseCommand` on `DispenseException`

- **Event Notification System (decoupled subsystems)**:
  - Event bus: `src/aura/events/EventBus.java`
  - Concrete events: `LowStockEvent`, `HardwareFailureEvent`, `EmergencyModeEvent`
  - Subscriptions: `AuraConsoleApp.registerEvents()`

- **Concurrent Transactions (no overselling)**:
  - Inventory lock protects reserve/confirm/restore: `InventoryManager` uses `ReentrantLock`
  - Concurrency demo: `AuraConsoleApp.concurrentStressTest()` launches multiple buyer threads; reservations prevent stock going below zero

- **Delayed Hardware Response**:
  - Simulated delay: `BaseDispenser.dispense(..., delayed=true, ...)` uses `Thread.sleep`
  - Demo path: console supports delayed purchases (used in concurrency test and power-saving description)

- **Event Priority**:
  - Priorities: `EventPriority`
  - Batch dispatch sorting: `EventBus.publishBatch(...)`
  - Demo: console’s “Emergency priority broadcast” publishes a mixed batch where emergency dispatch happens first

- **Purchase limit during emergencies**:
  - Emergency ration cap policy: `EmergencyRationPolicy` (default max 2 units)
  - Wired in via factory: `EmergencyReliefKioskFactory.createInventoryPolicy()`

## 6) Simulation scenarios demonstrated by the console app

The console UI provides interactive scenarios that demonstrate Path A behavior:

- **Dynamic pricing change**: switch between Standard, Discounted (percent), and Emergency pricing at runtime.
- **Operational mode changes**: Active ↔ Power Saving ↔ Maintenance ↔ Emergency Lockdown; run diagnostics to view derived status.
- **Emergency priority broadcast**: publish a mixed batch of events where emergency is dispatched first; triggers global mode+pricing override.
- **Hardware fault + rollback**:
  - Operator can mark hardware faulted (publishes a `HardwareFailureEvent`).
  - A forced dispenser failure in purchase triggers rollback and then runs the failure handler chain.
- **Concurrent transaction integrity**: multiple buyer threads purchase simultaneously; inventory reservations and locking prevent overselling.
- **Derived inventory view**: shows stock, reserved, and derived available quantity (including hardware constraints).
- **Transaction history view**: shows recent persisted transactions from `data/transactions.csv`.

## 7) How to run (minimal, without external docs)

From the repository root, compile and run the Java sources. One simple approach:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
java -cp out Main
```

Notes:

- The application creates/uses a `data/` folder in the working directory.
- On exit it persists:
  - `data/inventory.csv`
  - `data/transactions.csv`
  - `data/config.txt`

## 8) Object-oriented principles demonstrated

- **Encapsulation**:
  - Inventory mutation and locking is contained within `InventoryManager`.
  - Hardware operational status is managed via `HardwareRegistry`.

- **Abstraction**:
  - `KioskFactory`, `PricingStrategy`, `KioskState`, `TransactionCommand`, and event interfaces separate “what” from “how”.

- **Inheritance / polymorphism**:
  - Concrete states implement `KioskState`.
  - Concrete pricing strategies implement `PricingStrategy`.
  - Emergency policy extends `BasicInventoryPolicy`.
  - Failure handlers extend `FailureHandler`.

- **Low coupling**:
  - The UI interacts mainly through `KioskFacade`.
  - Events flow through `EventBus` instead of direct calls between subsystems.
  - Factories encapsulate kiosk-family configuration.

## 9) Limitations and simplifications (relative to the PDF’s full system vision)

This implementation focuses on Path A’s adaptive behavior patterns. Some PDF subsystems are simplified for a console simulation:

- **Payment providers**: verification is simulated via `VerificationModule` (e.g., non-empty userId), not via multi-provider payment adapters/gateways.
- **City monitoring center**: represented by event subscribers printing notices, rather than an external service integration.
- **Hardware layer**: modeled as registry/modules and a single shared dispenser implementation (`BaseDispenser`) configured by factories; enough to demonstrate hardware dependency, faults, and delayed response.

