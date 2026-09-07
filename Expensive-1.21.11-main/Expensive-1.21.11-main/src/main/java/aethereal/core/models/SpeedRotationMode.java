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

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpeedRotationMode extends RotationMode {
    public float yawSpeed;
    public float pitchSpeed;

    public SpeedRotationMode(float f, float f2) {
        super("Linear Rotation");
        this.yawSpeed = f;
        this.pitchSpeed = f2;
    }

    @Override
    public Rotation process(Rotation class007Var, Rotation class007Var2, Vec3d vec3d, Entity entity) {
        Rotation class007VarCalculateRotationDifference= RotationMath.INSTANCE.calculateRotationDifference(class007Var2, class007Var);
        float yaw= class007VarCalculateRotationDifference.getYaw();
        float pitch= class007VarCalculateRotationDifference.getPitch();
        float fHypot= (float) Math.hypot(Math.abs(yaw), Math.abs(pitch));
        float fAbs= Math.abs(yaw / fHypot) * this.yawSpeed;
        float fAbs2= Math.abs(pitch / fHypot) * this.pitchSpeed;
        return new Rotation(class007Var.getYaw() + MathHelper.clamp(yaw, -fAbs, fAbs), class007Var.getPitch() + MathHelper.clamp(pitch, -fAbs2, fAbs2));
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.0d, 0.0d, 0.0d);
    }
}
