package aura.bootstrap;

import aura.domain.Product;
import aura.domain.ProductCategory;
import aura.hardware.HardwareModule;
import aura.hardware.HardwareRegistry;
import aura.inventory.InventoryManager;

public class SeedData {
    public static void hardware(HardwareRegistry registry) {
        registry.register(new HardwareModule("MED-SEC-1", "Medication Lockbox", true));
        registry.register(new HardwareModule("REF-1", "Refrigeration Unit", true));
        registry.register(new HardwareModule("BULK-1", "Bulk Relief Hopper", true));
        registry.register(new HardwareModule("NET-1", "Network Uplink", true));
    }

    public static void inventory(InventoryManager inventory) {
        inventory.putItem(new Product("MED-ASP", "Aspirin", ProductCategory.PHARMACY, 5.00, "MED-SEC-1"), 30, 0);
        inventory.putItem(new Product("MED-AMX", "Amoxicillin", ProductCategory.PHARMACY, 10.00, "MED-SEC-1"), 48, 0);
        inventory.putItem(new Product("FOOD-COF", "Coffee Cup", ProductCategory.FOOD, 5.00, ""), 40, 0);
        inventory.putItem(new Product("FOOD-SND", "Sandwich", ProductCategory.FOOD, 8.00, "REF-1"), 25, 0);
        inventory.putItem(new Product("FOOD-ICE", "Ice Cream", ProductCategory.FOOD, 4.00, "REF-1"), 15, 0);
        inventory.putItem(new Product("REL-WTR", "Water Bottle", ProductCategory.EMERGENCY, 5.00, "BULK-1"), 100, 0);
        inventory.putItem(new Product("REL-AID", "First Aid Kit", ProductCategory.EMERGENCY, 15.00, "BULK-1"), 50, 0);
    }
}
