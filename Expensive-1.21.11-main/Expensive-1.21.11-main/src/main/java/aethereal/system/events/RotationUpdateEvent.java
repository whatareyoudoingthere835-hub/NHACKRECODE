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


public final class RotationUpdateEvent implements Event {
    public final EventPhase stage;

    public RotationUpdateEvent(EventPhase class346Var) {
        this.stage = class346Var;
    }

    public boolean isPost() {
        return this.stage == EventPhase.POST;
    }

    public boolean isPre() {
        return this.stage == EventPhase.PRE;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "stage=" + this.stage + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.stage);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RotationUpdateEvent)) return false;
        RotationUpdateEvent o= (RotationUpdateEvent) obj;
        return java.util.Objects.equals(this.stage, o.stage);
    }
public EventPhase stage() {
        return this.stage;
    }
}
