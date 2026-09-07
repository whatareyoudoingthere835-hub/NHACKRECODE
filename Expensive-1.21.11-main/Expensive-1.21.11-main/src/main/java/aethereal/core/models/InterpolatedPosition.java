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

import net.minecraft.util.math.Vec3d;

public final class InterpolatedPosition {
    public Vec3d pos;
    public Vec3d lastPos;

    public void plusAssign(Vec3d vec3d) {
        this.lastPos = this.pos;
        this.pos = this.pos.add(vec3d);
    }

    public Vec3d interpolate(float f) {
        return new Vec3d(this.lastPos.x + ((this.pos.x - this.lastPos.x) * ((double) f)), this.lastPos.y + ((this.pos.y - this.lastPos.y) * ((double) f)), this.lastPos.z + ((this.pos.z - this.lastPos.z) * ((double) f)));
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public Vec3d getLastPos() {
        return this.lastPos;
    }

    public InterpolatedPosition(Vec3d vec3d, Vec3d vec3d2) {
        this.pos = vec3d;
        this.lastPos = vec3d2;
    }
}
