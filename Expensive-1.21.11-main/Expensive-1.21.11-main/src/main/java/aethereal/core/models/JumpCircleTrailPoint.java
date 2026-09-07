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

public final class JumpCircleTrailPoint {
    public final long spawnTime = System.currentTimeMillis();
    public final Vec3d position;
    final JumpCircleModule module;

    public JumpCircleTrailPoint(JumpCircleModule class555Var, Vec3d vec3d) {
        this.module = class555Var;
        this.position = vec3d;
    }

    public float getProgress() {
        return (System.currentTimeMillis() - this.spawnTime) / this.module.lifetimeSetting.currentValue();
    }
}
