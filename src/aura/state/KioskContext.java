package aura.state;

// Design Pattern: State Context
public class KioskContext {
    private KioskState state = new ActiveMode();

    public KioskState state() {
        return state;
    }

    public void transitionTo(KioskState next) {
        state = next;
    }
}
