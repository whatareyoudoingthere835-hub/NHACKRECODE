package ru.expensive.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.event.EventHandler;
import ru.expensive.implement.events.packet.PacketEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoFriendDamageModule extends Module {
    public NoFriendDamageModule() {
        super("NoFriendDamage", "No Friend Damage", ModuleCategory.COMBAT);
    }

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        if (!packetEvent.isSend() || mc.world == null) {
            return;
        }
        if (packetEvent.getPacket() instanceof PlayerInteractEntityC2SPacket interactPacket) {
            Entity entity = mc.world.getEntityById(interactPacket.entityId);
            if (!(entity instanceof PlayerEntity) || !isAttack(interactPacket)) {
                return;
            }
            if (FriendRepository.isFriend(entity.getName().getString())) {
                packetEvent.cancel();
            }
        }
    }

    private boolean isAttack(PlayerInteractEntityC2SPacket packet) {
        final boolean[] attack = {false};
        packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
            @Override
            public void interact(Hand hand) {
            }

            @Override
            public void interactAt(Hand hand, Vec3d pos) {
            }

            @Override
            public void attack() {
                attack[0] = true;
            }
        });
        return attack[0];
    }
}

