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


public final class ModuleStateEvent implements Event {
    public final Module module;
    public final boolean moduleState;

    public ModuleStateEvent(Module class605Var, boolean z) {
        this.module = class605Var;
        this.moduleState = z;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "module=" + this.module + ", " + "moduleState=" + this.moduleState + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.module, this.moduleState);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ModuleStateEvent)) return false;
        ModuleStateEvent o= (ModuleStateEvent) obj;
        return java.util.Objects.equals(this.module, o.module) && java.util.Objects.equals(this.moduleState, o.moduleState);
    }
public Module module() {
        return this.module;
    }

    public boolean moduleState() {
        return this.moduleState;
    }
}
