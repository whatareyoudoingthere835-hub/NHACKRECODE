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

public class TrajectoryPoint {
    public final int tick;
    public final Vec3d velocity;
    public final Vec3d position;
    public final Vec3d pos;

    public TrajectoryPoint(int i, Vec3d vec3d, Vec3d vec3d2, Vec3d vec3d3) {
        this.tick = i;
        this.velocity = vec3d;
        this.position = vec3d2;
        this.pos = vec3d3;
    }

    public Vec3d pos() {
        return this.pos;
    }

    public int tick() {
        return this.tick;
    }
}
