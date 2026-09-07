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


public final class CharInput implements InputEvent {
    public final int codePoint;
    public final int mods;

    public CharInput(int i, int i2) {
        this.codePoint = i;
        this.mods = i2;
    }

    @Override
    public InputType type() {
        return InputType.CHAR;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "codePoint=" + this.codePoint + ", " + "mods=" + this.mods + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.codePoint, this.mods);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CharInput)) return false;
        CharInput o= (CharInput) obj;
        return java.util.Objects.equals(this.codePoint, o.codePoint) && java.util.Objects.equals(this.mods, o.mods);
    }
public int codePoint() {
        return this.codePoint;
    }

    public int mods() {
        return this.mods;
    }
}
