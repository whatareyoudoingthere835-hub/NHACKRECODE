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

import net.minecraft.util.math.Vec2f;

public final class RotationDelta {
    public final float deltaYaw;
    public final float deltaPitch;

    public RotationDelta(float f, float f2) {
        this.deltaYaw = f;
        this.deltaPitch = f2;
    }

    public float length() {
        return (float) Math.hypot(this.deltaYaw, this.deltaPitch);
    }

    public Vec2f toVec2f() {
        return new Vec2f(this.deltaYaw, this.deltaPitch);
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "deltaYaw=" + this.deltaYaw + ", " + "deltaPitch=" + this.deltaPitch + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.deltaYaw, this.deltaPitch);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RotationDelta)) return false;
        RotationDelta o= (RotationDelta) obj;
        return java.util.Objects.equals(this.deltaYaw, o.deltaYaw) && java.util.Objects.equals(this.deltaPitch, o.deltaPitch);
    }
public float deltaYaw() {
        return this.deltaYaw;
    }

    public float deltaPitch() {
        return this.deltaPitch;
    }
}
