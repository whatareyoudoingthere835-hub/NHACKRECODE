package aethereal.features.modules.combat;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

@Aliases(aliases = {"No Friend Damage", "Anti Friend Hit", "No Friendly Fire", "Friend Protection", "No Friend Attack"})
public class NoFriendDamageModule extends Module {
    public NoFriendDamageModule() {
        super(ModuleTab.COMBAT, "No Friend Damage");
        register(PacketSendEvent.class, class037Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded()) {
                if ((class037Var.getPacket()) instanceof PlayerInteractEntityC2SPacket packet ) {
                    PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket= packet;
                    Entity entityById= Mc.INSTANCE.getWorld().getEntityById(playerInteractEntityC2SPacket.entityId);
                    if (entityById == null || !isFriendAttack(entityById, playerInteractEntityC2SPacket.type.getType())) {
                        return;
                    }
                    class037Var.cancel();
                }
            }
        });
    }

    public boolean isFriendAttack(Entity entity, PlayerInteractEntityC2SPacket.InteractType interactType) {
        if (FriendManager.isFriend(entity.getName().getString()) && interactType == PlayerInteractEntityC2SPacket.InteractType.ATTACK) {
            return entity instanceof PlayerEntity;
        }
        return false;
    }
}
