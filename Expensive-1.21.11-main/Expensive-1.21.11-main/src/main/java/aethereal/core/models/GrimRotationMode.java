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

import java.security.SecureRandom;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class GrimRotationMode extends RotationMode {
    public GrimRotationMode() {
        super("SnapRotation");
    }

    @Override
    public Rotation process(Rotation class007Var, Rotation class007Var2, Vec3d vec3d, Entity entity) {
        float random;
        float random2;
        AttackAuraModule class878Var= (AttackAuraModule) Expensive.INSTANCE.moduleRepository().get(AttackAuraModule.class);
        Stopwatch class314VarAttackTimer= class878Var.attackTimer();
        Rotation class007VarCalculateRotationDifference= RotationMath.INSTANCE.calculateRotationDifference(class007Var2, class007Var);
        float yaw= class007VarCalculateRotationDifference.getYaw();
        float pitch= class007VarCalculateRotationDifference.getPitch();
        float fHypot= (float) Math.hypot(Math.abs(yaw), Math.abs(pitch));
        boolean z= entity != null && class878Var.canAttack(Mc.INSTANCE.getPlayer(), 1);
        float fAbs= Math.abs(yaw / fHypot) * 180.0f;
        float fAbs2= Math.abs(pitch / fHypot) * 180.0f;
        if (z) {
            random = 1.0f;
        } else {
            random = class314VarAttackTimer.hasElapsed(50L) ? FastMathUtils.getRandom(0.4f, 0.7f) : -0.2f;
        }
        float f= random;
        if (z) {
            random2 = 1.0f;
        } else {
            random2 = class314VarAttackTimer.hasElapsed(50L) ? FastMathUtils.getRandom(0.4f, 0.7f) : -0.2f;
        }
        return new Rotation(MathHelper.lerp(f, class007Var.getYaw(), class007Var.getYaw() + MathHelper.clamp(yaw, -fAbs, fAbs)), MathHelper.lerp(random2, class007Var.getPitch(), class007Var.getPitch() + MathHelper.clamp(pitch, -fAbs2, fAbs2)));
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.0d, 0.0d, 0.0d);
    }

    public float randomFactor() {
        return MathHelper.lerp(new SecureRandom().nextFloat(), 2, 4);
    }
}
