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

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class PlayerSnapshot {
    public final Box box;
    public final Vec3d pos;
    public final boolean verticalCollision;

    public PlayerSnapshot(Box box, Vec3d vec3d, boolean z) {
        this.box = box;
        this.pos = vec3d;
        this.verticalCollision = z;
    }

    public static PlayerSnapshot from(LivingEntity livingEntity) {
        return new PlayerSnapshot(livingEntity.getBoundingBox(), livingEntity.getEntityPos(), livingEntity.verticalCollision);
    }
}
