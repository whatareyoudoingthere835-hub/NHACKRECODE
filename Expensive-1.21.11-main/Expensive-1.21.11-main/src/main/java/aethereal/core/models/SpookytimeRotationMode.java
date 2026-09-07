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

public class SpookytimeRotationMode extends RotationMode {
    public SpookytimeRotationMode() {
        super("SpookyTime");
    }

    @Override
    public Rotation process(Rotation current, Rotation target, Vec3d vec3d, Entity entity) {
        if (target == null) {
            return current;
        }
        if (current == null) {
            return target;
        }
        AttackAuraModule attackAura= (AttackAuraModule) Expensive.INSTANCE.moduleRepository().get(AttackAuraModule.class);
        Stopwatch attackTimer= attackAura.attackTimer();
        int hitCounter= attackAura.hitCounter();
        float speed= new SecureRandom().nextBoolean() ? 0.5f : 0.3f;
        Rotation diff= RotationMath.INSTANCE.calculateRotationDifference(target, current);
        float yaw= diff.getYaw();
        float pitch= diff.getPitch();
        float hypot= (float) Math.hypot(Math.abs(yaw), Math.abs(pitch));
        float maxYaw= Math.abs(yaw / hypot) * 180.0f;
        float maxPitch= Math.abs(pitch / hypot) * 180.0f;
        float clampYaw= MathHelper.clamp(yaw, -maxYaw, maxYaw);
        float clampPitch= MathHelper.clamp(pitch, -maxPitch, maxPitch);

        if (attackAura.target() != null && attackAura.canCrit(Mc.INSTANCE.getPlayer(), 0)) {
            return current.add(new Rotation(clampYaw, clampPitch));
        }

        float factor= !attackTimer.hasElapsed(500L) ? -0.15f : speed;
        float time1= (System.currentTimeMillis() % 2000) / 70.0f;
        float time2= (System.currentTimeMillis() % 1000) / 70.0f;
        Rotation offset;
        switch (hitCounter % 4) {
            case 0:
                offset = new Rotation((float) Math.cos(time1), (float) Math.sin(time2));
                break;
            case 1:
                offset = new Rotation((float) Math.sin(time1), (float) Math.cos(time2));
                break;
            case 2:
                offset = new Rotation((float) Math.sin(time1), (float) (-Math.cos(time2)));
                break;
            default:
                offset = new Rotation((float) (-Math.cos(time1)), (float) Math.sin(time2));
                break;
        }
        return new Rotation(
            MathHelper.lerp(MathHelper.clamp(randomLerp(factor, factor + 0.25f), 0.0f, 1.0f), current.getYaw(), current.getYaw() + clampYaw) + (!attackTimer.hasElapsed(1000L) ? randomLerp(7.0f, 14.0f) * offset.getYaw() : 0.0f),
            MathHelper.lerp(MathHelper.clamp(randomLerp(factor, factor + 0.25f), 0.0f, 1.0f), current.getPitch(), current.getPitch() + clampPitch) + (!attackTimer.hasElapsed(1000L) ? randomLerp(2.5f, 5.0f) * offset.getPitch() : 0.0f)
        );
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.0d, 0.0d, 0.0d);
    }

    public float randomLerp(float f, float f2) {
        return MathHelper.lerp(new SecureRandom().nextFloat(), f, f2);
    }
}
