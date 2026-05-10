package aura.events;

public enum EventPriority {
    EMERGENCY(0),
    HIGH(1),
    NORMAL(2);

    private final int rank;

    EventPriority(int rank) {
        this.rank = rank;
    }

    public int rank() {
        return rank;
    }
}
