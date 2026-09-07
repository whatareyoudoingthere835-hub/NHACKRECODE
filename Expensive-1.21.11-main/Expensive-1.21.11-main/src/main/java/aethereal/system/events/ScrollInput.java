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


public final class ScrollInput implements InputEvent {
    public final double deltaY;

    public ScrollInput(double d) {
        this.deltaY = d;
    }

    @Override
    public InputType type() {
        return InputType.SCROLL;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "deltaY=" + this.deltaY + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.deltaY);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ScrollInput)) return false;
        ScrollInput o= (ScrollInput) obj;
        return java.util.Objects.equals(this.deltaY, o.deltaY);
    }
public double deltaY() {
        return this.deltaY;
    }
}
