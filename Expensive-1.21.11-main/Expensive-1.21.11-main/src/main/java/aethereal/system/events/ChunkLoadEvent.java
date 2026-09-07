package aethereal.system.events;
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

import net.minecraft.util.math.ChunkPos;

public final class ChunkLoadEvent implements Event {
    public final ChunkPos chunkPos;

    public ChunkLoadEvent(ChunkPos chunkPos) {
        this.chunkPos = chunkPos;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "chunkPos=" + this.chunkPos + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.chunkPos);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChunkLoadEvent)) return false;
        ChunkLoadEvent o= (ChunkLoadEvent) obj;
        return java.util.Objects.equals(this.chunkPos, o.chunkPos);
    }
public ChunkPos chunkPos() {
        return this.chunkPos;
    }
}
