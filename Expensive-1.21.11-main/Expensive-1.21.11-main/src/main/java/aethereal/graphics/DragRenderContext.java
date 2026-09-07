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

import net.minecraft.client.Mouse;
import net.minecraft.client.util.math.MatrixStack;

public final class DragRenderContext {
    public final ScreenResolution resolution;
    public final Mouse mousePosition;
    public final MatrixStack matrixStack;

    public final GraphicsDrawEngine drawEngine;

    public final Theme theme;
    public final float aF;

    public DragRenderContext(ScreenResolution class710Var, Mouse mouse, MatrixStack matrixStack, GraphicsDrawEngine class154Var, Theme class760Var, float f) {
        this.resolution = class710Var;
        this.mousePosition = mouse;
        this.matrixStack = matrixStack;
        this.drawEngine = class154Var;
        this.theme = class760Var;
        this.aF = f;
    }

    public float mouseX() {
        return (float) (this.mousePosition.getX() / ((double) this.aF));
    }

    public float mouseY() {
        return (float) (this.mousePosition.getY() / ((double) this.aF));
    }

    public void sizeAnimation(MatrixStack matrixStack, float f, float f2, float f3) {
        matrixStack.translate(f / 2.0f, f2 / 2.0f, 0.0f);
        matrixStack.scale(f3, f3, 0.0f);
        matrixStack.translate((-f) / 2.0f, (-f2) / 2.0f, 0.0f);
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "resolution=" + this.resolution + ", " + "mousePosition=" + this.mousePosition + ", " + "matrixStack=" + this.matrixStack + ", " + "drawEngine=" + this.drawEngine + ", " + "theme=" + this.theme + ", " + "aF=" + this.aF + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.resolution, this.mousePosition, this.matrixStack, this.drawEngine, this.theme, this.aF);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DragRenderContext)) return false;
        DragRenderContext o= (DragRenderContext) obj;
        return java.util.Objects.equals(this.resolution, o.resolution) && java.util.Objects.equals(this.mousePosition, o.mousePosition) && java.util.Objects.equals(this.matrixStack, o.matrixStack) && java.util.Objects.equals(this.drawEngine, o.drawEngine) && java.util.Objects.equals(this.theme, o.theme) && java.util.Objects.equals(this.aF, o.aF);
    }
public ScreenResolution resolution() {
        return this.resolution;
    }

    public Mouse mousePosition() {
        return this.mousePosition;
    }

    public MatrixStack matrixStack() {
        return this.matrixStack;
    }

    public GraphicsDrawEngine drawEngine() {
        return this.drawEngine;
    }

    public Theme theme() {
        return this.theme;
    }

    public float scaleFactor() {
        return this.aF;
    }
}
