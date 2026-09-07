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

import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;

public final class HwTrackedItem {
    public final Item item;
    public final Vec3d vec;
    public final String world;
    public final int anarchy;
    public final double time;
    public final float boxSize;

    public HwTrackedItem(Item item, Vec3d vec3d, String str, int i, double d, float f) {
        this.item = item;
        this.vec = vec3d;
        this.world = str;
        this.anarchy = i;
        this.time = d;
        this.boxSize = f;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "item=" + this.item + ", " + "vec=" + this.vec + ", " + "world=" + this.world + ", " + "anarchy=" + this.anarchy + ", " + "time=" + this.time + ", " + "boxSize=" + this.boxSize + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.item, this.vec, this.world, this.anarchy, this.time, this.boxSize);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HwTrackedItem)) return false;
        HwTrackedItem o= (HwTrackedItem) obj;
        return java.util.Objects.equals(this.item, o.item) && java.util.Objects.equals(this.vec, o.vec) && java.util.Objects.equals(this.world, o.world) && java.util.Objects.equals(this.anarchy, o.anarchy) && java.util.Objects.equals(this.time, o.time) && java.util.Objects.equals(this.boxSize, o.boxSize);
    }
public Item item() {
        return this.item;
    }

    public Vec3d vec() {
        return this.vec;
    }

    public String world() {
        return this.world;
    }

    public int anarchy() {
        return this.anarchy;
    }

    public double time() {
        return this.time;
    }

    public float boxSize() {
        return this.boxSize;
    }
}
