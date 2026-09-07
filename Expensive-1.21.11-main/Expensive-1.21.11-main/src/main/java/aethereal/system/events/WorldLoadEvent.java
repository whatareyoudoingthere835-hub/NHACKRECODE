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

import net.minecraft.world.World;

public final class WorldLoadEvent implements Event {
    public final World world;

    public WorldLoadEvent(World world) {
        this.world = world;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "world=" + this.world + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.world);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WorldLoadEvent)) return false;
        WorldLoadEvent o= (WorldLoadEvent) obj;
        return java.util.Objects.equals(this.world, o.world);
    }
public World world() {
        return this.world;
    }
}
