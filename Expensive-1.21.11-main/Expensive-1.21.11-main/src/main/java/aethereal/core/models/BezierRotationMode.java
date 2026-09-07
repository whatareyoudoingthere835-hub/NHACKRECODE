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
import net.minecraft.util.math.Vec3d;

public class BezierRotationMode extends RotationMode {
    public Rotation startRotation;
    public Rotation targetRotation;
    public float controlOffsetX;
    public float controlOffsetY;
    public float progress;
    public long lastTimeNanos;

    public BezierRotationMode() {
        super("Bezier Rotation");
        this.startRotation = Rotation.ZERO;
        this.targetRotation = Rotation.ZERO;
        this.progress = 1.0f;
        this.lastTimeNanos = System.nanoTime();
    }

    @Override
    public Rotation process(Rotation class007Var, Rotation class007Var2, Vec3d vec3d, Entity entity) {
        boolean z= Math.abs(class007Var2.getYaw() - this.targetRotation.getYaw()) > 3.0f || Math.abs(class007Var2.getPitch() - this.targetRotation.getPitch()) > 3.0f;
        long jNanoTime= System.nanoTime();
        float f= (jNanoTime - this.lastTimeNanos) / 1.0E9f;
        this.lastTimeNanos = jNanoTime;
        if (z && this.progress >= 1.0f) {
            this.controlOffsetX = ((float) (Math.random() - 0.5d)) * 2.0f;
            this.controlOffsetY = (float) (Math.random() - 0.5d);
            this.startRotation = class007Var;
            this.targetRotation = class007Var2;
            this.progress = 0.0f;
        }
        float length= 1.0f;
        if (vec3d != null) {
            length = (float) vec3d.length();
        }
        float fClamp= FastMathUtils.clamp(length / 10.0f, 0.2f, 0.1f);
        if (this.progress < 1.0f) {
            this.progress += f / fClamp;
            this.progress = FastMathUtils.clamp(this.progress, 0.0f, 1.0f);
        }
        return FastMathUtils.quadraticBezier(this.startRotation, this.targetRotation, this.progress, this.controlOffsetX, this.controlOffsetY);
    }

    public float easeInOut(float f) {
        return f < 0.5f ? 2.0f * f * f : (-1.0f) + ((4.0f - (2.0f * f)) * f);
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.0d, 0.0d, 0.0d);
    }
}
