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

public class NeuroRotationMode extends RotationMode {
    public NeuroRotationMode() {
        super("Neuro");
    }

    @Override
    public Rotation process(Rotation current, Rotation target, Vec3d vec3d, Entity entity) {
        if (target == null) {
            return current;
        }
        if (current == null) {
            return target;
        }
        float yawDiff= MathHelper.wrapDegrees(target.getYaw() - current.getYaw());
        float pitchDiff= target.getPitch() - current.getPitch();
        float yawStep= MathHelper.clamp(yawDiff * 0.45f, -65.0f, 65.0f);
        float pitchStep= MathHelper.clamp(pitchDiff * 0.45f, -45.0f, 45.0f);
        if (Math.abs(yawStep) < 0.25f) {
            yawStep = yawDiff;
        }
        if (Math.abs(pitchStep) < 0.25f) {
            pitchStep = pitchDiff;
        }
        return new Rotation(current.getYaw() + yawStep, current.getPitch() + pitchStep);
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.0d, 0.0d, 0.0d);
    }
}
