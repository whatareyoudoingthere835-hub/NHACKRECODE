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

import org.joml.Matrix4f;

public class RenderedGlyph {
    public final int code;
    public final float minU;
    public final float maxU;
    public final float minV;
    public final float maxV;
    public final float advance;
    public final float bearingY;
    public final float width;
    public final float height;

    public RenderedGlyph(GlyphData class297Var, float f, float f2) {
        this.code = class297Var.unicode();
        this.advance = class297Var.advance();
        GlyphBounds class295VarAtlasBounds= class297Var.atlasBounds();
        if (class295VarAtlasBounds != null) {
            this.minU = class295VarAtlasBounds.left() / f;
            this.maxU = class295VarAtlasBounds.right() / f;
            this.minV = 1.0f - (class295VarAtlasBounds.top() / f2);
            this.maxV = 1.0f - (class295VarAtlasBounds.bottom() / f2);
        } else {
            this.maxV = 0.0f;
            this.minV = 0.0f;
            this.maxU = 0.0f;
            this.minU = 0.0f;
        }
        GlyphBounds class295VarPlaneBounds= class297Var.planeBounds();
        if (class295VarPlaneBounds != null) {
            this.width = class295VarPlaneBounds.right() - class295VarPlaneBounds.left();
            this.height = class295VarPlaneBounds.top() - class295VarPlaneBounds.bottom();
            this.bearingY = class295VarPlaneBounds.top();
        } else {
            this.bearingY = 0.0f;
            this.height = 0.0f;
            this.width = 0.0f;
        }
    }

    public float apply(Matrix4f matrix4f, GraphicsDrawEngine class154Var, float f, float f2, float f3, int i, int i2) {
        class154Var.textureMsdf(matrix4f, f2, f3 - (this.bearingY * f), this.width * f, this.height * f, this.minU, this.minV, this.maxU, this.maxV, i2, i);
        return this.advance * f;
    }

    public float apply(Matrix4f matrix4f, GraphicsDrawEngine class154Var, float f, float f2, float f3, int i, int i2, int i3, int i4, int i5) {
        class154Var.textureMsdf(matrix4f, f2, f3 - (this.bearingY * f), this.width * f, this.height * f, this.minU, this.minV, this.maxU, this.maxV, i5, i, i2, i3, i4);
        return this.advance * f;
    }

    public float getWidth(float f) {
        return this.advance * f;
    }

    public int getCode() {
        return this.code;
    }
}
