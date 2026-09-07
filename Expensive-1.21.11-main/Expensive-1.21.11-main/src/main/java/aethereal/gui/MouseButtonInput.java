package aethereal.gui;
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


public final class MouseButtonInput implements InputEvent {
    public final int button;
    public final MouseButtonAction action;

    public final MouseModifiers mods;

    public MouseButtonInput(int i, MouseButtonAction class706Var, MouseModifiers class707Var) {
        this.button = i;
        this.action = class706Var;
        this.mods = class707Var;
    }

    @Override
    public InputType type() {
        return InputType.BUTTON;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "button=" + this.button + ", " + "action=" + this.action + ", " + "mods=" + this.mods + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.button, this.action, this.mods);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MouseButtonInput)) return false;
        MouseButtonInput o= (MouseButtonInput) obj;
        return java.util.Objects.equals(this.button, o.button) && java.util.Objects.equals(this.action, o.action) && java.util.Objects.equals(this.mods, o.mods);
    }
public int button() {
        return this.button;
    }

    public MouseButtonAction action() {
        return this.action;
    }

    public MouseModifiers mods() {
        return this.mods;
    }
}
