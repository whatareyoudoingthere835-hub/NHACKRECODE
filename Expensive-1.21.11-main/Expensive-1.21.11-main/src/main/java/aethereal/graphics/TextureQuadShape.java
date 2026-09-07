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
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class TextureQuadShape extends RenderShape {
    public Matrix4f matrix4f;
    public float x, y, width, height;
    public Identifier texture;
    public int color;
    public boolean z, z2, z3;

    public TextureQuadShape() {
        this.state = RenderShapeState.POSITION_COLOR_QUADS;
    }

    public TextureQuadShape set(Matrix4f matrix4f, float f, float f2, float f3, float f4, Identifier identifier, int i, boolean z, boolean z2) {
        return set(matrix4f, f, f2, f3, f4, identifier, i, z, z2, false);
    }

    public TextureQuadShape set(Matrix4f matrix4f, float f, float f2, float f3, float f4, Identifier identifier, int i, boolean z, boolean z2, boolean z3) {
        if (this.matrix4f == null) {
            this.matrix4f = new Matrix4f(matrix4f);
        } else {
            this.matrix4f.set(matrix4f);
        }
        this.x = f;
        this.y = f2;
        this.width = f3;
        this.height = f4;
        this.texture = identifier;
        this.color = i;
        this.z = z;
        this.z2 = z2;
        this.z3 = z3;
        this.state = RenderShapeState.getTextureQuadState(identifier, z, z2, z3);
        return this;
    }

    @Override
    public void emit(MatrixStack matrixStack, BufferBuilder bufferBuilder) {
        ShapeRenderer.INSTANCE.emitTextureQuad(matrix4f, bufferBuilder, x, y, width, height, color);
    }
}
