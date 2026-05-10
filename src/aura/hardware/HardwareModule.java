package aura.hardware;

public class HardwareModule {
    private final String id;
    private final String name;
    private boolean operational;

    public HardwareModule(String id, String name, boolean operational) {
        this.id = id;
        this.name = name;
        this.operational = operational;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isOperational() {
        return operational;
    }

    public void setOperational(boolean operational) {
        this.operational = operational;
    }
}
