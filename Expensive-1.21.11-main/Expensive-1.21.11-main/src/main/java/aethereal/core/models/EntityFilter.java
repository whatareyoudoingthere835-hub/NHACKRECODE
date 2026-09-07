package aethereal.core.models;
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.Entity;

public final class EntityFilter {
    public final Set<EntityCategory> categories = new HashSet<EntityCategory>();

    public EntityFilter add(EntityCategory class096Var) {
        this.categories.add(class096Var);
        return this;
    }

    public boolean matches(Entity entity) {
        Iterator it= this.categories.iterator();
        while (it.hasNext()) {
            if (((EntityCategory) it.next()).matches(entity)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return this.categories.isEmpty();
    }

    public static EntityCategory resolve(Entity entity) {
        for (EntityCategory class096Var : EntityCategory.values()) {
            if (class096Var.matches(entity)) {
                return class096Var;
            }
        }
        return null;
    }

    public static EntityCategory resolve(Entity entity, EntityCategory... class096VarArr) {
        for (EntityCategory class096Var : class096VarArr) {
            if (class096Var.matches(entity)) {
                return class096Var;
            }
        }
        return null;
    }
}
