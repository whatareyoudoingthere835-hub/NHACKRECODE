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


public final class PixelPoint {
    public final int x;
    public final int y;

    public PixelPoint(double d, double d2) {
        this(round(d), round(d2));
    }

    public PixelPoint(int i, int i2) {
        this.x = i;
        this.y = i2;
    }

    public static int round(double d) {
        return (int) Math.round(d);
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "x=" + this.x + ", " + "y=" + this.y + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.x, this.y);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PixelPoint)) return false;
        PixelPoint o= (PixelPoint) obj;
        return java.util.Objects.equals(this.x, o.x) && java.util.Objects.equals(this.y, o.y);
    }
public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }
}
