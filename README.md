# Aura Retail OS

**Course:** Object Oriented Programming (IT620)

Aura Retail OS is a modular Java-based smart-city retail infrastructure system supporting autonomous kiosks (Pharmacy, Food, Emergency Relief). This project fulfills the **Path A: Adaptive Autonomous System** constraints for dynamic decision-making, adaptive behavior, and fault tolerance.

## Architecture & Design Patterns

The platform leverages pure object-oriented principles and 9 professional design patterns to handle extreme hardware variations, unexpected transactional errors, and system emergencies natively without monolithic complexity.

1. **Abstract Factory**  
   Dynamically instantiates kiosk-specific components (`Dispenser`, `VerificationModule`, `InventoryPolicy`).
   
2. **Command Pattern**  
   Models transactions into executable classes (`PurchaseItemCommand`) enforcing isolated state interactions decoupled from hardware mechanisms.
   
3. **Memento Pattern**  
   Responsible for keeping deep-copied inventory snapshots, enabling true atomic transaction behavior. If dispensing hardware falls into a runtime error mid-cycle, the transaction natively rolls back to its exact previous state.
   
4. **Strategy Pattern**  
   Allows the system to swap live price formulas instantly (e.g., `StandardPricing` -> `EmergencyPricing`) depending on external context and global alerts.
   
5. **State Pattern**  
   Maintains kiosk behavioral states Context (`ActiveMode`, `MaintenanceMode`, `EmergencyLockdownMode`), physically preventing or permitting tasks structurally to avoid massive embedded `if/else` checks.
   
6. **Chain of Responsibility**  
   Passes runtime anomalies (such as Hardware errors) through a sequential, decoupled pipeline of handlers (`RetryHandler -> RecalibrationHandler -> TechnicianAlertHandler`).
   
7. **Observer Pattern / EventBus**  
   Serves as a publish/subscribe communication layer. Subsystems broadcast custom `SystemEvent` forms across threads while registered listeners scale and adapt behaviors instantly across modules.
   
8. **Singleton**  
   Coordinates core configuration maps and event-buses homogeneously via the `CentralRegistry` mapping the smart city's scope cleanly.
   
9. **Facade Pattern**  
   Wraps all sprawling modules behind `KioskInterface`, providing external clients a seamless unified interface for restocking and purchasing items cleanly.

## Run Steps

Compile and execute the core `Main.java` orchestrator to run the full end-to-end simulation.

### Clone Repository

```bash
git clone https://github.com/Jeet0105/Aura-Retail-OS.git
cd Aura-Retail-OS
```

### Prerequisites
- Java JDK 8 or higher installed and available on `PATH`

### Build + Run (Command Prompt / Windows)
Run from the project root:

```cmd
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out Main
```

### Build + Run (PowerShell / Windows)
Run from the project root:

```powershell
if (Test-Path out) { Remove-Item out -Recurse -Force }
New-Item -ItemType Directory out | Out-Null
Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName | Set-Content sources.txt
javac -d out "@sources.txt"
java -cp out Main
```

## Simulation Demonstration

Running `Main` executes a structured demonstration of all major requirements and patterns:

1. **System Bootstrap**  
   Initializes `CentralRegistry` (Singleton), loads configuration, and sets up the `EventBus` observer channels.

2. **Pharmacy Kiosk Flow**  
   Restocks medicine, runs a normal purchase, then forces a hardware-failure purchase to prove transaction rollback using Memento.

3. **Failure Recovery Pipeline**  
   Publishes a `HardwareFailureEvent` and passes it through `RetryHandler -> RecalibrationHandler -> TechnicianAlertHandler` (Chain of Responsibility).

4. **Food Kiosk Flow**  
   Applies `DiscountedPricing`, executes a discounted purchase, and emits a `LowStockEvent` to trigger supply-chain alerts.

5. **Emergency Relief Flow**  
   Broadcasts `EmergencyModeActivated`, switches to `EmergencyLockdownMode`, swaps pricing to `EmergencyPricing`, and enforces rationing rules.

6. **State Coverage Test**  
   Demonstrates state transitions (`ActiveMode`, `PowerSavingMode`, `MaintenanceMode`) and behavior changes per state.

7. **Refund Processing**  
   Runs a refund transaction and verifies inventory correction.

8. **Concurrency Integrity Test**  
   Starts two simultaneous purchase threads and verifies final stock consistency (`expected = 30`) to demonstrate atomic operations.

9. **Persistence Summary**  
   Saves configuration and prints transaction summaries via the persistence layer.

If successful, the run ends with:

```text
=== Simulation Complete ===
```
