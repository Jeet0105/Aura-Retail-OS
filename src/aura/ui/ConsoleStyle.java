package aura.ui;

public class ConsoleStyle {
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";

    public static String color(String color, String value) {
        return color + value + RESET;
    }

    public static void banner(String title, String subtitle) {
        line();
        System.out.println(BOLD + CYAN + center(title, 74) + RESET);
        if (subtitle != null && !subtitle.isEmpty()) {
            System.out.println(DIM + center(subtitle, 74) + RESET);
        }
        line();
    }

    public static void panel(String title) {
        System.out.println();
        System.out.println(BOLD + BLUE + "+-- " + title + " " + repeat("-", Math.max(0, 65 - title.length())) + "+" + RESET);
    }

    public static void line() {
        System.out.println(CYAN + "+" + repeat("-", 74) + "+" + RESET);
    }

    public static String money(double value) {
        return "Rs." + String.format("%.2f", value);
    }

    private static String center(String value, int width) {
        if (value.length() >= width) {
            return value;
        }
        int left = (width - value.length()) / 2;
        return repeat(" ", left) + value;
    }

    private static String repeat(String value, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }
}
