package aethereal.system.events;
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

public final class MouseMoveInput {
    public final boolean intercepted;
    public final Mouse mouse;
    public final ScreenResolution resolution;
    public final float aE;

    public MouseMoveInput(boolean z, Mouse mouse, ScreenResolution class710Var, float f) {
        this.intercepted = z;
        this.mouse = mouse;
        this.resolution = class710Var;
        this.aE = f;
    }

    public boolean isWithinBounds(float f, float f2, float f3, float f4) {
        double x= this.mouse.getX() / ((double) this.aE);
        double y= this.mouse.getY() / ((double) this.aE);
        return x >= ((double) f) && x <= ((double) (f + f3)) && y >= ((double) f2) && y <= ((double) (f2 + f4));
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "intercepted=" + this.intercepted + ", " + "mouse=" + this.mouse + ", " + "resolution=" + this.resolution + ", " + "aE=" + this.aE + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.intercepted, this.mouse, this.resolution, this.aE);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MouseMoveInput)) return false;
        MouseMoveInput o= (MouseMoveInput) obj;
        return java.util.Objects.equals(this.intercepted, o.intercepted) && java.util.Objects.equals(this.mouse, o.mouse) && java.util.Objects.equals(this.resolution, o.resolution) && java.util.Objects.equals(this.aE, o.aE);
    }
public boolean intercepted() {
        return this.intercepted;
    }

    public Mouse mouse() {
        return this.mouse;
    }

    public ScreenResolution resolution() {
        return this.resolution;
    }

    public float scaleFactor() {
        return this.aE;
    }
}
