package aethereal.core.types;
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

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

public enum EntityCategory {
    ANIMAL {
        @Override
        public boolean matches(Entity entity) {
            return entity instanceof AnimalEntity;
        }
    },
    FRIEND {
        @Override
        public boolean matches(Entity entity) {
            return (entity instanceof PlayerEntity) && FriendManager.isFriend(entity.getName().getString());
        }
    },
    MOB {
        @Override
        public boolean matches(Entity entity) {
            return entity instanceof HostileEntity;
        }
    },
    PLAYER {
        @Override
        public boolean matches(Entity entity) {
            return entity instanceof PlayerEntity;
        }
    },
    SELF {
        @Override
        public boolean matches(Entity entity) {
            ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
            return player != null && entity != null && player.getId() == entity.getId();
        }
    };

    public abstract boolean matches(Entity entity);
}
