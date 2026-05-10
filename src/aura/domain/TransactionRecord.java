package aura.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionRecord {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String id;
    private final LocalDateTime timestamp;
    private final String userId;
    private final KioskType kioskType;
    private final String productId;
    private final String productName;
    private final int quantity;
    private final double finalPrice;
    private final TransactionStatus status;
    private final String note;

    public TransactionRecord(String id, LocalDateTime timestamp, String userId, KioskType kioskType,
                             String productId, String productName, int quantity, double finalPrice,
                             TransactionStatus status, String note) {
        this.id = id;
        this.timestamp = timestamp;
        this.userId = userId;
        this.kioskType = kioskType;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.finalPrice = finalPrice;
        this.status = status;
        this.note = note == null ? "" : note;
    }

    public static TransactionRecord now(String userId, KioskType kioskType, Product product, int quantity,
                                        double finalPrice, TransactionStatus status, String note) {
        String id = "TX-" + System.currentTimeMillis();
        return new TransactionRecord(id, LocalDateTime.now(), userId, kioskType, product.getId(),
                product.getName(), quantity, finalPrice, status, note);
    }

    public String toCsv() {
        return String.join(",",
                escape(id),
                escape(timestamp.format(FORMATTER)),
                escape(userId),
                escape(kioskType.name()),
                escape(productId),
                escape(productName),
                String.valueOf(quantity),
                String.format("%.2f", finalPrice),
                escape(status.name()),
                escape(note));
    }

    public String summaryLine() {
        return String.format("%s | %-10s | %-18s | qty=%d | Rs.%.2f | %s",
                timestamp.format(FORMATTER), status, productName, quantity, finalPrice, note);
    }

    private static String escape(String value) {
        String safe = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safe + "\"";
    }
}
