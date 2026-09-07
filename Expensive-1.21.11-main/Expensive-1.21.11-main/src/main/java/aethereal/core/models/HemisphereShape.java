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

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class HemisphereShape extends RenderShape {
    public Matrix4f matrix4f;
    public Vec3d center;
    public float radius;
    public int segments, color;
    public boolean cameraRelative;

    public HemisphereShape() {
        this.state = RenderShapeState.POSITION_COLOR_TRIANGLES;
    }

    public HemisphereShape set(Matrix4f matrix4f, Vec3d vec3d, float f, int i, int i2, boolean z) {
        if (this.matrix4f == null) {
            this.matrix4f = new Matrix4f(matrix4f);
        } else {
            this.matrix4f.set(matrix4f);
        }
        this.center = vec3d;
        this.radius = f;
        this.segments = i;
        this.color = i2;
        this.cameraRelative = z;
        return this;
    }

    @Override
    public void emit(MatrixStack matrixStack, BufferBuilder bufferBuilder) {
        if (center != null) {
            ShapeRenderer.INSTANCE.emitHemisphere(matrix4f, bufferBuilder, center, radius, segments, color, cameraRelative);
        }
    }
}
