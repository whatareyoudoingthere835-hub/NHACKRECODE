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


public final class WindowResizeEvent implements Event {
    public final int width;
    public final int height;

    public WindowResizeEvent(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "width=" + this.width + ", " + "height=" + this.height + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.width, this.height);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WindowResizeEvent)) return false;
        WindowResizeEvent o= (WindowResizeEvent) obj;
        return java.util.Objects.equals(this.width, o.width) && java.util.Objects.equals(this.height, o.height);
    }
public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }
}
