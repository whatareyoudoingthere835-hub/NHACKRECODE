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

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

public final class Render2DEvent implements Event {
    public final MatrixStack matrixStack;

    public final Render2DStage stage;

    public final RenderTickCounter tickCounter;

    public final DrawContext drawContext;

    public Render2DEvent(MatrixStack matrixStack, Render2DStage class312Var, RenderTickCounter renderTickCounter, DrawContext drawContext) {
        this.matrixStack = matrixStack;
        this.stage = class312Var;
        this.tickCounter = renderTickCounter;
        this.drawContext = drawContext;
    }

    public boolean isPost() {
        return this.stage == Render2DStage.POST;
    }

    public boolean isPre() {
        return this.stage == Render2DStage.PRE;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "matrixStack=" + this.matrixStack + ", " + "stage=" + this.stage + ", " + "tickCounter=" + this.tickCounter + ", " + "drawContext=" + this.drawContext + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.matrixStack, this.stage, this.tickCounter, this.drawContext);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Render2DEvent)) return false;
        Render2DEvent o= (Render2DEvent) obj;
        return java.util.Objects.equals(this.matrixStack, o.matrixStack) && java.util.Objects.equals(this.stage, o.stage) && java.util.Objects.equals(this.tickCounter, o.tickCounter) && java.util.Objects.equals(this.drawContext, o.drawContext);
    }
public MatrixStack matrixStack() {
        return this.matrixStack;
    }

    public Render2DStage stage() {
        return this.stage;
    }

    public RenderTickCounter tickCounter() {
        return this.tickCounter;
    }

    public DrawContext drawContext() {
        return this.drawContext;
    }
}
