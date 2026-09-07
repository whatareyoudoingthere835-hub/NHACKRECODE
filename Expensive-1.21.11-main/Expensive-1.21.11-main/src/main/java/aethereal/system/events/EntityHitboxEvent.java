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
import net.minecraft.util.math.Box;

public class EntityHitboxEvent extends CancellableEvent {
    public final Box box;
    public final Entity entity;

    public Box changedBox;

    public void setChangedBox(Box box) {
        this.changedBox = box;
    }

    public Box getBox() {
        return this.box;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Box getChangedBox() {
        return this.changedBox;
    }

    public EntityHitboxEvent(Box box, Entity entity) {
        this.box = box;
        this.entity = entity;
    }
}
