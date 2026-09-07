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

public final class HandledScreenRenderEvent implements Event {
    public final DrawContext drawContext;
    public final int backgroundWidth;
    public final int backgroundHeight;

    public HandledScreenRenderEvent(DrawContext drawContext, int i, int i2) {
        this.drawContext = drawContext;
        this.backgroundWidth = i;
        this.backgroundHeight = i2;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "drawContext=" + this.drawContext + ", " + "backgroundWidth=" + this.backgroundWidth + ", " + "backgroundHeight=" + this.backgroundHeight + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.drawContext, this.backgroundWidth, this.backgroundHeight);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HandledScreenRenderEvent)) return false;
        HandledScreenRenderEvent o= (HandledScreenRenderEvent) obj;
        return java.util.Objects.equals(this.drawContext, o.drawContext) && java.util.Objects.equals(this.backgroundWidth, o.backgroundWidth) && java.util.Objects.equals(this.backgroundHeight, o.backgroundHeight);
    }
public DrawContext drawContext() {
        return this.drawContext;
    }

    public int backgroundWidth() {
        return this.backgroundWidth;
    }

    public int backgroundHeight() {
        return this.backgroundHeight;
    }
}
