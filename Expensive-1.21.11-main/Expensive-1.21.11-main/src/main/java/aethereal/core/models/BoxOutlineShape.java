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
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public class BoxOutlineShape extends RenderShape {
    public Matrix4f matrix4f;
    public Box box;
    public int color, color2;
    public boolean z, z2, cameraRelative;
    public float width;

    public BoxOutlineShape() {
        this.state = RenderShapeState.POSITION_COLOR_LINES;
    }

    public BoxOutlineShape set(Matrix4f matrix4f, Box box, int i, int i2, boolean z, boolean z2, float f, boolean z3) {
        if (this.matrix4f == null) {
            this.matrix4f = new Matrix4f(matrix4f);
        } else {
            this.matrix4f.set(matrix4f);
        }
        this.box = box;
        this.color = i;
        this.color2 = i2;
        this.z = z;
        this.z2 = z2;
        this.width = f;
        this.cameraRelative = z3;
        return this;
    }

    @Override
    public void emit(MatrixStack matrixStack, BufferBuilder bufferBuilder) {
        if (box != null) {
            ShapeRenderer.INSTANCE.emitBoxOutline(matrix4f, bufferBuilder, box, color, color2, z, z2, cameraRelative);
        }
    }
}
