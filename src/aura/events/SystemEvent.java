package aura.events;

import java.time.LocalDateTime;

public interface SystemEvent {
    String type();

    EventPriority priority();

    String message();

    LocalDateTime occurredAt();
}
