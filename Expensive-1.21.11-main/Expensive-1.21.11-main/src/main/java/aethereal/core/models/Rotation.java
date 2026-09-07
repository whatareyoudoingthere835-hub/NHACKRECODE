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

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Rotation {
    public float yaw;
    public float pitch;
    public static Rotation ZERO = new Rotation(0.0f, 0.0f);

    public static Rotation playerRotation() {
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        return player == null ? ZERO : new Rotation(player.getYaw(), player.getPitch());
    }

    public Rotation random(float f) {
        return add(FastMathUtils.getRandom(-f, f), FastMathUtils.getRandom(-f, f));
    }

    public Rotation add(float f, float f2) {
        return new Rotation(this.yaw + f, FastMathUtils.clamp(this.pitch + f2, -90.0f, 90.0f));
    }

    public Rotation addYaw(float f) {
        this.yaw += f;
        return this;
    }

    public Rotation add(Rotation class007Var) {
        return new Rotation(this.yaw + class007Var.yaw, this.pitch + class007Var.pitch);
    }

    public static Rotation lookingAt(Vec3d vec3d, Vec3d vec3d2) {
        if (vec3d == null) {
            return playerRotation();
        }
        return RotationMath.INSTANCE.fromVec3d(vec3d.subtract(vec3d2).normalize());
    }

    public Vec3d getDirectionVector() {
        return RotationMath.INSTANCE.rotationToVector(this);
    }

    public float withFixedYaw(Rotation class007Var) {
        return class007Var.getYaw() + angleDifference(Mc.INSTANCE.getPlayer().getYaw(), class007Var.getYaw());
    }

    public static float angleDifference(float f, float f2) {
        return MathHelper.wrapDegrees(f - f2);
    }

    public float angleTo(Rotation class007Var) {
        return Math.min(rotationDeltaTo(class007Var).length(), 180.0f);
    }

    public RotationDelta rotationDeltaTo(Rotation class007Var) {
        return new RotationDelta(angleDifference(class007Var.yaw, this.yaw), angleDifference(class007Var.pitch, this.pitch));
    }

    public Rotation normalize() {
        Rotation currentRotation= PlayerRotationManager.INSTANCE.getCurrentRotation();
        double dComputeGcd= FastMathUtils.computeGcd();
        RotationDelta class397VarRotationDeltaTo= currentRotation.rotationDeltaTo(this);
        return new Rotation(currentRotation.yaw + ((float) (((double) ((int) (((double) class397VarRotationDeltaTo.deltaYaw()) / dComputeGcd))) * dComputeGcd)), FastMathUtils.clamp(currentRotation.pitch + ((float) (((double) ((int) (((double) class397VarRotationDeltaTo.deltaPitch()) / dComputeGcd))) * dComputeGcd)), -90.0f, 90.0f));
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setYaw(float f) {
        this.yaw = f;
    }

    public void setPitch(float f) {
        this.pitch = f;
    }

    public Rotation(float f, float f2) {
        this.yaw = f;
        this.pitch = f2;
    }
}
