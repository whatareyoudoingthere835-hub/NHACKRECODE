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

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;

public class ProjectileTrajectory {
    public final List<ChunkPos> chunks;
    public final Entity entity;
    public final ItemStack stack;
    public final List<TrajectoryPoint> steps;

    public ProjectileTrajectory(Entity entity, ItemStack itemStack, List list, List list2) {
        this.entity = entity;
        this.stack = itemStack;
        this.chunks = list;
        this.steps = list2;
    }

    public Entity entity() {
        return this.entity;
    }

    public ItemStack stack() {
        return this.stack;
    }

    public List steps() {
        return this.steps;
    }

    public boolean tick() {
        return this.entity.isRemoved();
    }
}
