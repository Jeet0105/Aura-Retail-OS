# Aura Retail OS

Interactive Java console simulation for IT620 Path A: Adaptive Autonomous System.

The project has been redeveloped around a fresh `aura.*` package structure with an attractive menu-driven console. Running `Main` opens the simulation directly; there is no scripted/demo mode selector.

## Run

From the project root:

```powershell
Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName | Set-Content sources.txt
javac -d out "@sources.txt"
java -cp out Main
```

## Interactive Features

- Switch between Pharmacy, Food, and Emergency Relief kiosk families.
- Purchase products with live computed pricing.
- Restock existing products or add new products.
- Change pricing strategy at runtime: Standard, Discounted, Emergency.
- Change operational mode: Active, Power Saving, Maintenance, Emergency Lockdown.
- Run diagnostics derived from kiosk state, hardware health, and network status.
- Fault or repair hardware modules and see hardware-dependent products become unavailable.
- Simulate delayed hardware plus forced failure rollback.
- Broadcast emergency events that override normal/high events through priority dispatch.
- Run concurrent purchase tests to verify stock is not oversold.
- Persist inventory, transactions, and config in `data/*.csv` / `data/config.txt`.

## Requirement Coverage

| Requirement Area | Implementation |
|---|---|
| OOP principles | Encapsulated domain models, interfaces, package separation, low coupling |
| Persistence | `aura.persistence.PersistenceManager` saves inventory, transactions, config |
| Singleton | `aura.core.CentralRegistry` |
| Abstract Factory | `aura.factory.*KioskFactory` creates compatible dispenser, verification, policy, pricing |
| Facade | `aura.facade.KioskFacade` exposes purchase, refund, restock, diagnostics |
| Command | `PurchaseCommand`, `RefundCommand`, `RestockCommand` |
| Memento rollback | `InventoryState` restores inventory after dispenser failure |
| State | Active, Power Saving, Maintenance, Emergency Lockdown |
| Strategy | Standard, Discounted, Emergency pricing |
| Chain of Responsibility | Retry -> Recalibration -> Technician Alert |
| Observer/EventBus | Low stock, hardware failure, emergency mode events |
| Event priority | Emergency events are dispatched before high/normal events |
| Concurrent transactions | Inventory reservations use locks before confirmation |
| Derived stock | Available stock = stock - reserved, blocked by hardware faults |
| Emergency purchase limit | Emergency ration policy limits purchases to 2 units |
| Hardware dependency | Products can depend on modules such as refrigeration or lockbox |

## Source Layout

```text
src/
  Main.java
  aura/
    bootstrap/      seed inventory and hardware
    command/        transaction commands
    core/           singleton registry
    domain/         product, inventory, transaction models
    events/         observer event bus and event types
    facade/         kiosk facade
    factory/        abstract factory kiosk families and components
    failure/        recovery chain
    hardware/       hardware modules and status registry
    inventory/      thread-safe inventory manager
    memento/        inventory snapshot
    persistence/    CSV/config persistence
    pricing/        strategy pricing implementations
    state/          operational modes
    ui/             redesigned interactive console
```

## Data Files

`data/inventory.csv` uses the redesigned schema:

```text
ProductID,Name,Category,BasePrice,Stock,Reserved,RequiredHardware
```

`data/transactions.csv` is normalized to:

```text
TransactionID,Timestamp,UserID,KioskType,ProductID,ProductName,Quantity,FinalPrice,Status,Note
```
