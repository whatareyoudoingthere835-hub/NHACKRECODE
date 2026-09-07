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


public final class MouseButtonAction {
    public final int action;

    public MouseButtonAction(int i) {
        this.action = i;
    }

    public boolean press() {
        return this.action == 1;
    }

    public boolean release() {
        return this.action == 0;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "action=" + this.action + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.action);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MouseButtonAction)) return false;
        MouseButtonAction o= (MouseButtonAction) obj;
        return java.util.Objects.equals(this.action, o.action);
    }
public int action() {
        return this.action;
    }
}
