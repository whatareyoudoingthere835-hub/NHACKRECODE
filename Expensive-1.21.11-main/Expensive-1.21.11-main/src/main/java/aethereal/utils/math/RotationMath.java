package aethereal.utils.math;
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

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class RotationMath {
    public static final RotationMath INSTANCE = new RotationMath();

    public Vec3d rotationToVector(Rotation class007Var) {
        float radians= (float) Math.toRadians(class007Var.getPitch());
        float radians2= (float) Math.toRadians(-class007Var.getYaw());
        float fCos= MathHelper.cos(radians2);
        float fSin= MathHelper.sin(radians2);
        float fCos2= MathHelper.cos(radians);
        return new Vec3d(fSin * fCos2, -MathHelper.sin(radians), fCos * fCos2);
    }

    public Rotation fromVec2f(Vec2f vec2f) {
        return new Rotation(vec2f.x, vec2f.y);
    }

    public Rotation fromVec3d(Vec3d vec3d) {
        return new Rotation((float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec3d.z, vec3d.x)) - 90.0d), (float) MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(vec3d.y, Math.hypot(vec3d.x, vec3d.z)))));
    }

    public Rotation fromVec3d(Vec3d vec3d, Vec3d vec3d2) {
        return fromVec3d(vec3d.subtract(vec3d2));
    }

    public Rotation calculateRotationDifference(Rotation class007Var, Rotation class007Var2) {
        return new Rotation(MathHelper.wrapDegrees(class007Var.getYaw() - class007Var2.getYaw()), class007Var.getPitch() - class007Var2.getPitch());
    }
}
