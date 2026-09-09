package thunder.hack.features.modules.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.events.impl.EventEntitySpawn;
import thunder.hack.features.modules.Module;

import java.util.Comparator;

public class PearlBait extends Module {
    public PearlBait() {
        super("PearlBait", Category.PLAYER);
    }

    @EventHandler
    public void onEntitySpawn(EventEntitySpawn e) {
        if (e.getEntity() instanceof EnderPearlEntity)
            mc.world.getPlayers().stream()
                    .min(Comparator.comparingDouble((p) -> p.squaredDistanceTo(new net.minecraft.util.math.Vec3d(e.getEntity().getX(), e.getEntity().getY(), e.getEntity().getZ()))))
                    .ifPresent((player) -> {
                        if (player.equals(mc.player) && mc.player.isOnGround()) {
                            mc.player.setVelocity(0, 0, 0);
                            thunder.hack.utility.player.InputUtility.setForward(false);
                            thunder.hack.utility.player.InputUtility.setStrafe(false);
                            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.0, mc.player.getZ(), false, mc.player.horizontalCollision));
                        }
                    });
    }
}