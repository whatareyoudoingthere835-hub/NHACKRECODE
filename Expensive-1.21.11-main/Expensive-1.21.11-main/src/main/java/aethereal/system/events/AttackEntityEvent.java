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

public final class AttackEntityEvent implements Event {
    public final Entity attacker;

    public AttackEntityEvent(Entity entity) {
        this.attacker = entity;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "attacker=" + this.attacker + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.attacker);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AttackEntityEvent)) return false;
        AttackEntityEvent o= (AttackEntityEvent) obj;
        return java.util.Objects.equals(this.attacker, o.attacker);
    }
public Entity attacker() {
        return this.attacker;
    }
}
