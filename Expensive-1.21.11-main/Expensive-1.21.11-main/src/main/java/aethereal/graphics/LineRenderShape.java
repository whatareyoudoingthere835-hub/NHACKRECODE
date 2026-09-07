package aethereal.graphics;
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

public class LineRenderShape extends RenderShape {
    public Matrix4f matrix4f;
    public Vec3d start, end;
    public int color;
    public float width;
    public boolean cameraRelative;

    public LineRenderShape() {
        this.state = RenderShapeState.POSITION_COLOR_LINES;
    }

    public LineRenderShape set(Matrix4f matrix4f, Vec3d vec3d, Vec3d vec3d2, int i, float f, boolean z) {
        if (this.matrix4f == null) {
            this.matrix4f = new Matrix4f(matrix4f);
        } else {
            this.matrix4f.set(matrix4f);
        }
        this.start = vec3d;
        this.end = vec3d2;
        this.color = i;
        this.width = f;
        this.cameraRelative = z;
        return this;
    }

    @Override
    public void emit(MatrixStack matrixStack, BufferBuilder bufferBuilder) {
        if (start != null && end != null) {
            ShapeRenderer.INSTANCE.emitLine(matrix4f, bufferBuilder, start, end, color, cameraRelative);
        }
    }
}
