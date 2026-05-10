package aura.domain;

public class Product {
    private final String id;
    private final String name;
    private final ProductCategory category;
    private final double basePrice;
    private final String requiredHardwareId;

    public Product(String id, String name, ProductCategory category, double basePrice, String requiredHardwareId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
        this.requiredHardwareId = requiredHardwareId == null ? "" : requiredHardwareId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getRequiredHardwareId() {
        return requiredHardwareId;
    }

    public boolean requiresHardware() {
        return !requiredHardwareId.isEmpty();
    }
}
