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

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityInterpolationEvent extends CancellableEvent {
    public final Vec3d original;
    public final Entity target;
    public final float tickDelta;
    public Vec3d changedVector = Vec3d.ZERO;

    public Vec3d original() {
        return this.original;
    }

    public Entity target() {
        return this.target;
    }

    public float tickDelta() {
        return this.tickDelta;
    }

    public Vec3d changedVector() {
        return this.changedVector;
    }

    public EntityInterpolationEvent(Vec3d vec3d, Entity entity, float f) {
        this.original = vec3d;
        this.target = entity;
        this.tickDelta = f;
    }

    public EntityInterpolationEvent changedVector(Vec3d vec3d) {
        this.changedVector = vec3d;
        return this;
    }
}
