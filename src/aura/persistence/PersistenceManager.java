package aura.persistence;

import aura.core.CentralRegistry;
import aura.domain.InventoryItem;
import aura.domain.KioskType;
import aura.domain.Product;
import aura.domain.ProductCategory;
import aura.domain.TransactionRecord;
import aura.domain.TransactionStatus;
import aura.inventory.InventoryManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersistenceManager {
    private static final String DATA_DIR = "data";
    private static final String INVENTORY_FILE = DATA_DIR + File.separator + "inventory.csv";
    private static final String TRANSACTION_FILE = DATA_DIR + File.separator + "transactions.csv";
    private static final String CONFIG_FILE = DATA_DIR + File.separator + "config.txt";
    private static final String TRANSACTION_HEADER = "TransactionID,Timestamp,UserID,KioskType,ProductID,ProductName,Quantity,FinalPrice,Status,Note";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PersistenceManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveInventory(InventoryManager inventory) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            writer.write("ProductID,Name,Category,BasePrice,Stock,Reserved,RequiredHardware\n");
            for (InventoryItem item : inventory.listItems()) {
                Product product = item.getProduct();
                writer.write(csv(product.getId()) + "," + csv(product.getName()) + "," + product.getCategory()
                        + "," + String.format("%.2f", product.getBasePrice())
                        + "," + item.getStock()
                        + "," + item.getReserved()
                        + "," + csv(product.getRequiredHardwareId()) + "\n");
            }
        } catch (IOException e) {
            System.out.println("Persistence error while saving inventory: " + e.getMessage());
        }
    }

    public boolean loadInventory(InventoryManager inventory) {
        File file = new File(INVENTORY_FILE);
        if (!file.exists()) {
            return false;
        }

        int loaded = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCsv(line);
                if (parts.size() >= 7) {
                    Product product = new Product(parts.get(0), parts.get(1),
                            ProductCategory.valueOf(parts.get(2)),
                            Double.parseDouble(parts.get(3)),
                            parts.get(6));
                    inventory.putItem(product, Integer.parseInt(parts.get(4)), Integer.parseInt(parts.get(5)));
                    loaded++;
                }
            }
            return loaded > 0;
        } catch (Exception e) {
            System.out.println("Persistence error while loading inventory: " + e.getMessage());
            return false;
        }
    }

    public void appendTransaction(TransactionRecord record) {
        File file = new File(TRANSACTION_FILE);
        boolean createHeader = !hasExpectedHeader(file, TRANSACTION_HEADER);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_FILE, !createHeader))) {
            if (createHeader) {
                writer.write(TRANSACTION_HEADER + "\n");
            }
            writer.write(record.toCsv());
            writer.write("\n");
        } catch (IOException e) {
            System.out.println("Persistence error while saving transaction: " + e.getMessage());
        }
    }

    private boolean hasExpectedHeader(File file, String expectedHeader) {
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return expectedHeader.equals(reader.readLine());
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> recentTransactions(int limit) {
        List<String> lines = new ArrayList<>();
        File file = new File(TRANSACTION_FILE);
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCsv(line);
                if (parts.size() >= 10) {
                    TransactionRecord record = new TransactionRecord(parts.get(0),
                            LocalDateTime.parse(parts.get(1), FORMATTER),
                            parts.get(2),
                            KioskType.valueOf(parts.get(3)),
                            parts.get(4),
                            parts.get(5),
                            Integer.parseInt(parts.get(6)),
                            Double.parseDouble(parts.get(7)),
                            TransactionStatus.valueOf(parts.get(8)),
                            parts.get(9));
                    lines.add(record.summaryLine());
                }
            }
        } catch (Exception e) {
            lines.add("Could not read transaction history: " + e.getMessage());
        }
        int from = Math.max(0, lines.size() - limit);
        return new ArrayList<>(lines.subList(from, lines.size()));
    }

    public void saveConfig(CentralRegistry registry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            for (Map.Entry<String, String> entry : registry.snapshot().entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Persistence error while saving config: " + e.getMessage());
        }
    }

    public void loadConfig(CentralRegistry registry) {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            registry.set("city", "Zephyrus");
            registry.set("emergency_mode", "false");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int index = line.indexOf('=');
                if (index > 0) {
                    registry.set(line.substring(0, index), line.substring(index + 1));
                }
            }
        } catch (IOException e) {
            System.out.println("Persistence error while loading config: " + e.getMessage());
        }
    }

    private static String csv(String value) {
        String safe = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safe + "\"";
    }

    private static List<String> parseCsv(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString());
        return values;
    }
}
