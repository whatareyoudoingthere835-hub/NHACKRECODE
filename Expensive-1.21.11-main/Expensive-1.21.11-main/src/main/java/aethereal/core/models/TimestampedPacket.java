package aethereal.core.models;
import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.features.modules.combat.*;
import aethereal.features.modules.movement.*;
import aethereal.features.modules.player.*;
import aethereal.features.modules.render.*;
import aethereal.features.modules.misc.*;
import aethereal.features.modules.earnings.*;
import aethereal.features.modules.autobuy.*;
import aethereal.features.commands.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.system.resources.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.accessors.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;

import net.minecraft.network.packet.Packet;

public final class TimestampedPacket {
    public final Packet<?> packet;
    public final long timestamp;

    public TimestampedPacket(Packet<?> packet, long j) {
        this.packet = packet;
        this.timestamp = j;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "timestamp=" + this.timestamp + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.timestamp);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TimestampedPacket)) return false;
        TimestampedPacket o= (TimestampedPacket) obj;
        return java.util.Objects.equals(this.timestamp, o.timestamp);
    }
public Packet<?> packet() {
        return this.packet;
    }

    public long timestamp() {
        return this.timestamp;
    }
}
