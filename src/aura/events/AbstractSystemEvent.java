package aura.events;

import java.time.LocalDateTime;

public abstract class AbstractSystemEvent implements SystemEvent {
    private final LocalDateTime occurredAt = LocalDateTime.now();

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}
