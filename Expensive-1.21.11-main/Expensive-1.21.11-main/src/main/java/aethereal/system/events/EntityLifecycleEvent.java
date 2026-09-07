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

import net.minecraft.entity.Entity;

public final class EntityLifecycleEvent implements Event {
    public final Entity entity;
    public final EntityLifecycleAction type;

    public EntityLifecycleEvent(Entity entity, EntityLifecycleAction class332Var) {
        this.entity = entity;
        this.type = class332Var;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "entity=" + this.entity + ", " + "type=" + this.type + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.entity, this.type);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityLifecycleEvent)) return false;
        EntityLifecycleEvent o= (EntityLifecycleEvent) obj;
        return java.util.Objects.equals(this.entity, o.entity) && java.util.Objects.equals(this.type, o.type);
    }
public Entity entity() {
        return this.entity;
    }

    public EntityLifecycleAction type() {
        return this.type;
    }
}
