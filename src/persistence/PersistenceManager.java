package persistence;

import java.io.*;
import java.util.*;

/**
 * Utility class for file-based persistence.
 * Handles saving/loading inventory, transactions, and configuration.
 */
public class PersistenceManager {
    private static final String DATA_DIR = "data";
    private static final String INVENTORY_FILE = "data/inventory.csv";
    private static final String TRANSACTION_FILE = "data/transactions.csv";
    private static final String CONFIG_FILE = "data/config.txt";
    private List<String> transactionHistory;

    public PersistenceManager() {
        this.transactionHistory = new ArrayList<>();
        createDataDirectory();
    }

    private void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdir();
            System.out.println("[Persistence] Created data directory");
        }
    }

    public void saveInventory(Map<String, Integer> inventory) {
        try {
            FileWriter writer = new FileWriter(INVENTORY_FILE);
            writer.write("ProductID,Stock\n");
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
            writer.close();
            System.out.println("[Persistence] Saved inventory snapshot");
        } catch (IOException e) {
            System.err.println("[Persistence] Error saving inventory: " + e.getMessage());
        }
    }

    public void loadInventory(Map<String, Integer> inventory) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    inventory.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
            reader.close();
            System.out.println("[Persistence] Loaded inventory from file");
        } catch (FileNotFoundException e) {
            System.out.println("[Persistence] No existing inventory file, starting fresh");
        } catch (IOException e) {
            System.err.println("[Persistence] Error loading inventory: " + e.getMessage());
        }
    }

    public void recordTransaction(String userId, String productId, int amount, double price, String status) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String transaction = timestamp + " | User: " + userId + " | Product: " + productId + 
                           " | Qty: " + amount + " | Price: ₹" + price + " | Status: " + status;
        transactionHistory.add(transaction);
        saveTransactions();
    }

    private void saveTransactions() {
        try {
            FileWriter writer = new FileWriter(TRANSACTION_FILE);
            writer.write("Timestamp,UserID,ProductID,Quantity,Price,Status\n");
            for (String tx : transactionHistory) {
                // Parse the human-readable format and convert to proper CSV
                // Format: "timestamp | User: userId | Product: productId | Qty: amount | Price: ₹price | Status: status"
                String[] parts = tx.split(" \\| ");
                if (parts.length >= 6) {
                    String timestamp = parts[0].trim();
                    String userId = parts[1].replace("User: ", "").trim();
                    String productId = parts[2].replace("Product: ", "").trim();
                    String quantity = parts[3].replace("Qty: ", "").trim();
                    String price = parts[4].replace("Price: ₹", "").trim();
                    String status = parts[5].replace("Status: ", "").trim();
                    writer.write(timestamp + "," + userId + "," + productId + "," + quantity + "," + price + "," + status + "\n");
                }
            }
            writer.close();
            System.out.println("[Persistence] Saved transactions to CSV");
        } catch (IOException e) {
            System.err.println("[Persistence] Error saving transactions: " + e.getMessage());
        }
    }

    public void saveConfiguration(Map<String, Object> config) {
        try {
            FileWriter writer = new FileWriter(CONFIG_FILE);
            writer.write("# Aura Retail OS Configuration\n");
            writer.write("# Generated: " + new Date() + "\n\n");
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                writer.write(entry.getKey() + " = " + entry.getValue() + "\n");
            }
            writer.close();
            System.out.println("[Persistence] Saved system configuration");
        } catch (IOException e) {
            System.err.println("[Persistence] Error saving configuration: " + e.getMessage());
        }
    }

    public void loadConfiguration(Map<String, Object> config) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(" = ");
                if (parts.length == 2) {
                    config.put(parts[0], parts[1]);
                }
            }
            reader.close();
            System.out.println("[Persistence] Loaded system configuration");
        } catch (FileNotFoundException e) {
            System.out.println("[Persistence] No existing configuration file");
        } catch (IOException e) {
            System.err.println("[Persistence] Error loading configuration: " + e.getMessage());
        }
    }

    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public void printTransactionSummary() {
        System.out.println("\n========== TRANSACTION HISTORY ==========");
        if (transactionHistory.isEmpty()) {
            System.out.println("  (No transactions recorded)");
        } else {
            for (String tx : transactionHistory) {
                System.out.println("  " + tx);
            }
        }
        System.out.println("Total transactions: " + transactionHistory.size());
    }
}

