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


public final class RegisteredListener<T extends Event> {
    public final EventCallback<T> callback;

    public final EventPriority priority;

    public RegisteredListener(EventCallback<T> class058Var, EventPriority class396Var) {
        this.callback = class058Var;
        this.priority = class396Var;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "callback=" + this.callback + ", " + "priority=" + this.priority + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.callback, this.priority);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RegisteredListener)) return false;
        RegisteredListener o= (RegisteredListener) obj;
        return java.util.Objects.equals(this.callback, o.callback) && java.util.Objects.equals(this.priority, o.priority);
    }
public EventCallback<T> callback() {
        return this.callback;
    }

    public EventPriority priority() {
        return this.priority;
    }
}
