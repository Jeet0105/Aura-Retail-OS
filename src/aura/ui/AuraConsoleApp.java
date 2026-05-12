package aura.ui;

import aura.bootstrap.SeedData;
import aura.core.CentralRegistry;
import aura.domain.InventoryItem;
import aura.domain.KioskType;
import aura.domain.Product;
import aura.domain.ProductCategory;
import aura.domain.TransactionRecord;
import aura.domain.TransactionResult;
import aura.events.EmergencyModeEvent;
import aura.events.EventBus;
import aura.events.HardwareFailureEvent;
import aura.events.LowStockEvent;
import aura.events.SystemEvent;
import aura.facade.KioskFacade;
import aura.factory.EmergencyReliefKioskFactory;
import aura.factory.FoodKioskFactory;
import aura.factory.KioskFactory;
import aura.factory.PharmacyKioskFactory;
import aura.factory.components.EmergencyRationPolicy;
import aura.failure.FailureHandler;
import aura.failure.RecalibrationHandler;
import aura.failure.RetryHandler;
import aura.failure.TechnicianAlertHandler;
import aura.hardware.HardwareModule;
import aura.hardware.HardwareRegistry;
import aura.inventory.InventoryManager;
import aura.persistence.PersistenceManager;
import aura.pricing.DiscountedPricing;
import aura.pricing.EmergencyPricing;
import aura.pricing.StandardPricing;
import aura.state.ActiveMode;
import aura.state.EmergencyLockdownMode;
import aura.state.MaintenanceMode;
import aura.state.PowerSavingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AuraConsoleApp {
    private static final int EMERGENCY_RATION_MAX_UNITS = 2;

    private final Scanner scanner = new Scanner(System.in);
    private final CentralRegistry registry = CentralRegistry.getInstance();
    private final PersistenceManager persistence = new PersistenceManager();
    private final InventoryManager inventory = new InventoryManager();
    private final HardwareRegistry hardware = new HardwareRegistry();
    private final EventBus eventBus = new EventBus();
    private final Map<KioskType, KioskFacade> kiosks = new LinkedHashMap<>();
    private KioskFacade activeKiosk;

    public void run() {
        initialize();
        boolean running = true;
        while (running) {
            dashboard();
            switch (readInt("Select action", 0, 11)) {
                case 1:
                    switchKiosk();
                    break;
                case 2:
                    purchase(false, false);
                    break;
                case 3:
                    restock();
                    break;
                case 4:
                    changePricing();
                    break;
                case 5:
                    changeMode();
                    break;
                case 6:
                    hardwareConsole();
                    break;
                case 7:
                    purchase(true, true);
                    break;
                case 8:
                    emergencyBroadcast();
                    break;
                case 9:
                    concurrentStressTest();
                    break;
                case 10:
                    showInventory();
                    pause();
                    break;
                case 11:
                    showTransactions();
                    pause();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    break;
            }
        }
        persistence.saveInventory(inventory);
        persistence.saveConfig(registry);
        ConsoleStyle.banner("Saved. Aura Retail OS session complete.", "inventory.csv, transactions.csv, config.txt updated");
    }

    private void initialize() {
        persistence.loadConfig(registry);
        SeedData.hardware(hardware);
        if (!persistence.loadInventory(inventory)) {
            SeedData.inventory(inventory);
            persistence.saveInventory(inventory);
        }
        registerEvents();
        createKiosk(new PharmacyKioskFactory());
        createKiosk(new FoodKioskFactory());
        createKiosk(new EmergencyReliefKioskFactory());
        activeKiosk = kiosks.get(KioskType.PHARMACY);
        if ("true".equals(registry.get("emergency_mode", "false"))) {
            applySystemEmergencyToAllKiosks(true);
        }
    }

    private void registerEvents() {
        eventBus.subscribe("LOW_STOCK", event -> notice(ConsoleStyle.YELLOW, "SUPPLY CHAIN", event.message()));
        eventBus.subscribe("HARDWARE_FAILURE", event -> notice(ConsoleStyle.RED, "HARDWARE", event.message()));
        eventBus.subscribe("EMERGENCY_MODE", event -> {
            notice(ConsoleStyle.RED, "PRIORITY EVENT", event.message());
            boolean wasEmergency = "true".equals(registry.get("emergency_mode", "false"));
            String nextFlag = wasEmergency ? "false" : "true";
            registry.set("emergency_mode", nextFlag);
            boolean nowEmergency = "true".equals(nextFlag);
            applySystemEmergencyToAllKiosks(nowEmergency);
        });
    }

    private void applySystemEmergencyToAllKiosks(boolean enabled) {
        for (KioskFacade kiosk : kiosks.values()) {
            if (enabled) {
                kiosk.setState(new EmergencyLockdownMode());
                kiosk.setPricing(new EmergencyPricing());
                kiosk.setInventoryPolicy(new EmergencyRationPolicy(EMERGENCY_RATION_MAX_UNITS));
            } else {
                kiosk.restoreFactoryOperationalDefaults();
            }
        }
    }

    private void createKiosk(KioskFactory factory) {
        FailureHandler retry = new RetryHandler();
        retry.link(new RecalibrationHandler()).link(new TechnicianAlertHandler());
        kiosks.put(factory.type(), new KioskFacade(factory, inventory, hardware, eventBus, retry));
    }

    private boolean emergencyRationPurchaseCapActive() {
        return "true".equals(registry.get("emergency_mode", "false"))
                || activeKiosk.state() instanceof EmergencyLockdownMode;
    }

    private void syncActiveKioskInventoryPolicyWithRegistry() {
        if ("true".equals(registry.get("emergency_mode", "false"))) {
            activeKiosk.setInventoryPolicy(new EmergencyRationPolicy(EMERGENCY_RATION_MAX_UNITS));
        } else {
            activeKiosk.resetInventoryPolicyToFactoryDefault();
        }
    }

    private void dashboard() {
        ConsoleStyle.banner("AURA RETAIL OS", "Adaptive autonomous kiosk simulator");
        System.out.printf("%sCity:%s %s   %sActive Kiosk:%s %s   %sMode:%s %s%n",
                ConsoleStyle.BOLD, ConsoleStyle.RESET, registry.get("city", "Zephyrus"),
                ConsoleStyle.BOLD, ConsoleStyle.RESET, activeKiosk.type().label(),
                ConsoleStyle.BOLD, ConsoleStyle.RESET, activeKiosk.state().name());
        System.out.printf("%sPricing:%s %s   %sEmergency:%s %s%n",
                ConsoleStyle.BOLD, ConsoleStyle.RESET, activeKiosk.pricing().name(),
                ConsoleStyle.BOLD, ConsoleStyle.RESET, registry.get("emergency_mode", "false"));

        ConsoleStyle.panel("Interactive Scenarios");
        System.out.println("  1. Switch kiosk family        7. Hardware-failure rollback");
        System.out.println("  2. Purchase product           8. Emergency priority broadcast");
        System.out.println("  3. Restock / add product      9. Concurrent transaction test");
        System.out.println("  4. Change pricing strategy   10. View derived inventory");
        System.out.println("  5. Change operational mode   11. View transaction history");
        System.out.println("  6. Hardware controls          0. Save and exit");
    }

    private void switchKiosk() {
        ConsoleStyle.panel("Kiosk Family");
        List<KioskFacade> list = new ArrayList<>(kiosks.values());
        for (int i = 0; i < list.size(); i++) {
            KioskFacade kiosk = list.get(i);
            System.out.printf("  %d. %-18s default pricing: %s%n", i + 1, kiosk.type().label(), kiosk.pricing().name());
        }
        System.out.println("  0. Back");
        int choice = readInt("Choose kiosk", 0, list.size());
        if (choice == 0) {
            return;
        }
        activeKiosk = list.get(choice - 1);
        notice(ConsoleStyle.GREEN, "KIOSK", "Now operating " + activeKiosk.type().label());
        pause();
    }

    private void purchase(boolean delayed, boolean forceFailure) {
        ConsoleStyle.panel(forceFailure ? "Rollback Scenario" : "Purchase Product");
        Product product = chooseProduct(false);
        if (product == null) {
            return;
        }
        String userId = readText("User ID");
        int maxPurchaseQty = emergencyRationPurchaseCapActive() ? EMERGENCY_RATION_MAX_UNITS : 999;
        int quantity = readInt("Quantity", 1, maxPurchaseQty);
        System.out.println("  Pricing preview: " + activeKiosk.pricing().name()
                + " -> " + ConsoleStyle.money(activeKiosk.pricing().price(product, quantity)));
        TransactionResult result = activeKiosk.purchaseItem(userId, product, quantity, delayed, forceFailure);
        record(userId, result);
        persistence.saveInventory(inventory);
        printResult(result);
        pause();
    }

    private void restock() {
        ConsoleStyle.panel("Restock Inventory");
        Product product;
        System.out.println("  1. Add brand new product");
        System.out.println("  2. Restock existing product");
        System.out.println("  0. Back");
        int action = readInt("Restock action", 0, 2);
        if (action == 0) {
            return;
        }
        if (action == 1) {
            product = createProduct();
            if (product == null) {
                return;
            }
        } else {
            product = chooseProduct(true);
            if (product == null) {
                return;
            }
        }
        int quantity = readInt("Quantity to add", 1, 10000);
        TransactionResult result = activeKiosk.restockInventory(product, quantity);
        record("SYSTEM", result);
        persistence.saveInventory(inventory);
        printResult(result);
        pause();
    }

    private Product createProduct() {
        String id = readText("Product ID");
        String name = readText("Product name");
        ProductCategory category = categoryFor(activeKiosk.type());
        double basePrice = readDouble("Base price");
        String hardwareId = "";
        if (yesNo("Does this product depend on hardware")) {
            HardwareModule module = chooseHardware();
            if (module == null) {
                return null;
            }
            hardwareId = module == null ? "" : module.getId();
        }
        return new Product(id, name, category, basePrice, hardwareId);
    }

    private void changePricing() {
        ConsoleStyle.panel("Pricing Strategy");
        System.out.println("  1. Standard pricing");
        System.out.println("  2. Discounted pricing");
        System.out.println("  3. Emergency pricing");
        System.out.println("  0. Back");
        int choice = readInt("Strategy", 0, 3);
        if (choice == 0) {
            return;
        }
        if (choice == 1) {
            activeKiosk.setPricing(new StandardPricing());
        } else if (choice == 2) {
            activeKiosk.setPricing(new DiscountedPricing(readDouble("Discount percent")));
        } else {
            activeKiosk.setPricing(new EmergencyPricing());
        }
        notice(ConsoleStyle.GREEN, "PRICING", "Strategy changed to " + activeKiosk.pricing().name());
        pause();
    }

    private void changeMode() {
        ConsoleStyle.panel("Operational Mode");
        System.out.println("  1. Active");
        System.out.println("  2. Power Saving");
        System.out.println("  3. Maintenance");
        System.out.println("  4. Emergency Lockdown");
        System.out.println("  5. Run diagnostics");
        System.out.println("  0. Back");
        int choice = readInt("Mode", 0, 5);
        if (choice == 0) {
            return;
        }
        if (choice == 1) {
            activeKiosk.setState(new ActiveMode());
            syncActiveKioskInventoryPolicyWithRegistry();
        } else if (choice == 2) {
            activeKiosk.setState(new PowerSavingMode());
            syncActiveKioskInventoryPolicyWithRegistry();
        } else if (choice == 3) {
            activeKiosk.setState(new MaintenanceMode());
            syncActiveKioskInventoryPolicyWithRegistry();
        } else if (choice == 4) {
            activeKiosk.setState(new EmergencyLockdownMode());
            activeKiosk.setInventoryPolicy(new EmergencyRationPolicy(EMERGENCY_RATION_MAX_UNITS));
        } else {
            System.out.println(activeKiosk.diagnostics());
        }
        if (choice < 5) {
            notice(ConsoleStyle.GREEN, "STATE", "Kiosk is now in " + activeKiosk.state().name());
        }
        pause();
    }

    private void hardwareConsole() {
        ConsoleStyle.panel("Hardware Controls");
        showHardware();
        System.out.println("  1. Report hardware fault");
        System.out.println("  2. Repair hardware");
        System.out.println("  3. Diagnostics");
        System.out.println("  0. Back");
        int choice = readInt("Action", 0, 3);
        if (choice == 0) {
            return;
        }
        if (choice == 3) {
            System.out.println(activeKiosk.diagnostics());
            pause();
            return;
        }
        HardwareModule module = chooseHardware();
        if (module == null) {
            return;
        }
        if (choice == 1) {
            hardware.markFaulted(module.getId());
            eventBus.publish(new HardwareFailureEvent(module.getId(), "operator reported fault"));
        } else {
            hardware.repair(module.getId());
            notice(ConsoleStyle.GREEN, "HARDWARE", module.getName() + " repaired");
        }
        persistence.saveInventory(inventory);
        pause();
    }

    private void emergencyBroadcast() {
        ConsoleStyle.panel("Emergency Priority Broadcast");
        String reason = readText("Emergency reason");
        List<SystemEvent> events = Arrays.asList(
                new LowStockEvent("demo normal event", 2, 5),
                new HardwareFailureEvent("NET-1", "queued network warning"),
                new EmergencyModeEvent(reason));
        System.out.println("Publishing a mixed batch. Emergency priority will dispatch first.");
        eventBus.publishBatch(events);
        persistence.saveConfig(registry);
        pause();
    }

    private void concurrentStressTest() {
        ConsoleStyle.panel("Concurrent Transaction Integrity");
        Product product = chooseProduct(false);
        if (product == null) {
            return;
        }
        int threadCount = readInt("Number of simultaneous buyers", 2, 10);
        int quantity = readInt("Quantity per buyer", 1, 20);
        int before = inventory.totalStock(product.getId());
        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= threadCount; i++) {
            final int buyer = i;
            Thread thread = new Thread(() -> {
                TransactionResult result = activeKiosk.purchaseItem("Concurrent-" + buyer, product, quantity, true, false);
                record("Concurrent-" + buyer, result);
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        int after = inventory.totalStock(product.getId());
        persistence.saveInventory(inventory);
        notice(ConsoleStyle.GREEN, "CONCURRENCY",
                "Before=" + before + ", after=" + after + ", no stock can go below zero because reservations are locked.");
        pause();
    }

    private Product chooseProduct(boolean allowAnyAccepted) {
        List<Product> products = new ArrayList<>();
        for (InventoryItem item : inventory.listItems()) {
            Product product = item.getProduct();
            if (allowAnyAccepted || activeKiosk.accepts(product)) {
                products.add(product);
            }
        }
        if (products.isEmpty()) {
            notice(ConsoleStyle.RED, "PRODUCT", "No products available for this kiosk.");
            pause();
            return null;
        }
        System.out.println("  Active pricing strategy: " + activeKiosk.pricing().name());
        System.out.printf("  %-3s %-12s %-20s %-10s %-12s %-10s %-12s%n",
                "#", "ID", "Name", "Base", "Unit Now", "Available", "Hardware");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.printf("  %-3d %-12s %-20s %-10s %-12s %-10d %-12s%n",
                    i + 1,
                    product.getId(),
                    product.getName(),
                    ConsoleStyle.money(product.getBasePrice()),
                    ConsoleStyle.money(activeKiosk.pricing().price(product, 1)),
                    inventory.availableStock(product, hardware),
                    product.requiresHardware() ? product.getRequiredHardwareId() : "none");
        }
        System.out.println("  0. Back");
        int choice = readInt("Product", 0, products.size());
        if (choice == 0) {
            return null;
        }
        return products.get(choice - 1);
    }

    private void showInventory() {
        ConsoleStyle.panel("Derived Inventory");
        System.out.printf("  %-12s %-20s %-11s %-8s %-9s %-10s %-12s%n",
                "ID", "Name", "Category", "Stock", "Reserved", "Available", "Hardware");
        for (InventoryItem item : inventory.listItems()) {
            Product product = item.getProduct();
            System.out.printf("  %-12s %-20s %-11s %-8d %-9d %-10d %-12s%n",
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    item.getStock(),
                    item.getReserved(),
                    inventory.availableStock(product, hardware),
                    product.requiresHardware() ? product.getRequiredHardwareId() : "none");
        }
    }

    private void showHardware() {
        System.out.printf("  %-12s %-24s %-12s%n", "ID", "Module", "Status");
        for (HardwareModule module : hardware.list()) {
            System.out.printf("  %-12s %-24s %-12s%n", module.getId(), module.getName(),
                    module.isOperational() ? "ONLINE" : "FAULTED");
        }
    }

    private void showTransactions() {
        ConsoleStyle.panel("Recent Transactions");
        List<String> lines = persistence.recentTransactions(12);
        if (lines.isEmpty()) {
            System.out.println("  No transactions recorded yet.");
            return;
        }
        for (String line : lines) {
            System.out.println("  " + line);
        }
    }

    private HardwareModule chooseHardware() {
        List<HardwareModule> modules = hardware.list();
        for (int i = 0; i < modules.size(); i++) {
            HardwareModule module = modules.get(i);
            System.out.printf("  %d. %-24s %s%n", i + 1, module.getName(), module.isOperational() ? "ONLINE" : "FAULTED");
        }
        System.out.println("  0. Back");
        int choice = readInt("Hardware module", 0, modules.size());
        if (choice == 0) {
            return null;
        }
        return modules.get(choice - 1);
    }

    private void record(String userId, TransactionResult result) {
        persistence.appendTransaction(TransactionRecord.now(userId, activeKiosk.type(), result.getProduct(),
                result.getQuantity(), result.getFinalPrice(), result.getStatus(), result.getMessage()));
    }

    private void printResult(TransactionResult result) {
        String color = result.isSuccess() ? ConsoleStyle.GREEN : ConsoleStyle.RED;
        notice(color, result.getStatus().name(), result.getMessage() + " | final price " + ConsoleStyle.money(result.getFinalPrice()));
    }

    private ProductCategory categoryFor(KioskType type) {
        if (type == KioskType.PHARMACY) {
            return ProductCategory.PHARMACY;
        }
        if (type == KioskType.FOOD) {
            return ProductCategory.FOOD;
        }
        return ProductCategory.EMERGENCY;
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(ConsoleStyle.BOLD + prompt + " [" + min + "-" + max + "]: " + ConsoleStyle.RESET);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("  Enter a number from " + min + " to " + max + ".");
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(ConsoleStyle.BOLD + prompt + ": " + ConsoleStyle.RESET);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("  Enter a valid non-negative number.");
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(ConsoleStyle.BOLD + prompt + ": " + ConsoleStyle.RESET);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  Value cannot be blank.");
        }
    }

    private boolean yesNo(String prompt) {
        while (true) {
            System.out.print(ConsoleStyle.BOLD + prompt + " [y/n]: " + ConsoleStyle.RESET);
            String value = scanner.nextLine().trim().toLowerCase();
            if (value.equals("y") || value.equals("yes")) {
                return true;
            }
            if (value.equals("n") || value.equals("no")) {
                return false;
            }
        }
    }

    private void pause() {
        System.out.print(ConsoleStyle.DIM + "\nPress ENTER to continue..." + ConsoleStyle.RESET);
        scanner.nextLine();
    }

    private void notice(String color, String label, String message) {
        System.out.println(ConsoleStyle.color(color, "[" + label + "] ") + message);
    }
}
