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


public final class WatermarkStar {
    public final float xOff;
    public final float yOff;
    public final float w;
    public final float h;
    public final float alpha;

    public WatermarkStar(float f, float f2, float f3, float f4, float f5) {
        this.xOff = f;
        this.yOff = f2;
        this.w = f3;
        this.h = f4;
        this.alpha = f5;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "xOff=" + this.xOff + ", " + "yOff=" + this.yOff + ", " + "w=" + this.w + ", " + "h=" + this.h + ", " + "alpha=" + this.alpha + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.xOff, this.yOff, this.w, this.h, this.alpha);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WatermarkStar)) return false;
        WatermarkStar o= (WatermarkStar) obj;
        return java.util.Objects.equals(this.xOff, o.xOff) && java.util.Objects.equals(this.yOff, o.yOff) && java.util.Objects.equals(this.w, o.w) && java.util.Objects.equals(this.h, o.h) && java.util.Objects.equals(this.alpha, o.alpha);
    }
public float xOff() {
        return this.xOff;
    }

    public float yOff() {
        return this.yOff;
    }

    public float w() {
        return this.w;
    }

    public float h() {
        return this.h;
    }

    public float alpha() {
        return this.alpha;
    }
}
