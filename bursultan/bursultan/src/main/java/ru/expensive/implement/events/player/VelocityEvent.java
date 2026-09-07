package ru.expensive.implement.events.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.packet.Packet;
import ru.expensive.api.event.events.Event;
import ru.expensive.api.event.events.Cancellable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VelocityEvent implements Event, Cancellable {

    final Packet<?> packet;
    double x, y, z;
    boolean cancelled;

    public VelocityEvent(Packet<?> packet, double x, double y, double z) {
        this.packet = packet;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}