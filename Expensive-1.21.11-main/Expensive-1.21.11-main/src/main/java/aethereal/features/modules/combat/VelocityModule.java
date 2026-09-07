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

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VelocityModule extends Module {
    public ModeSetting<VelocityMode> mode;
    final Mc mc;
    public boolean verticalKnockback;
    public int grimTicks;
    public boolean pendingGrim;

    public VelocityModule() {
        super(ModuleTab.COMBAT, "Velocity");
        this.mode = new ModeSetting(Lang.MODE).values(VelocityMode.class);
        this.mc = Mc.INSTANCE;
        this.verticalKnockback = false;
        this.grimTicks = 0;
        addSettings(this.mode);
        register(PlayerTickEvent.class, class130Var -> {
            this.grimTicks--;
            if (isState() && this.mc.isWorldLoaded() && this.mode.isSelected(VelocityMode.GRIM) && this.pendingGrim) {
                ClientPlayerEntity player= this.mc.getPlayer();
                BlockPos blockPos= player.getBlockPos();
                PacketSender.sendPacket(new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround(), player.horizontalCollision));
                PacketSender.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
                this.pendingGrim = false;
            }
        });
        register(PacketReceiveEvent.class, class051Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                if ((class051Var.getPacket()) instanceof EntityVelocityUpdateS2CPacket packet ) {
                    EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket= packet;
                    if (entityVelocityUpdateS2CPacket.getEntityId() == this.mc.getPlayer().getId()) {
                        switch (((VelocityMode) this.mode.currentValue()).ordinal()) {
                            case 0:
                                class051Var.cancel();
                                break;
                            case 1:
                            this.verticalKnockback = entityVelocityUpdateS2CPacket.getVelocity().x == 0.0d && entityVelocityUpdateS2CPacket.getVelocity().z == 0.0d && entityVelocityUpdateS2CPacket.getVelocity().y < 0.0d;
                                break;
                            case 2:
                                if (this.grimTicks >= 2) {
                                    return;
                                }
                                class051Var.cancel();
                                this.pendingGrim = true;
                                break;
                        }
                    } else {
                        return;
                    }
                }
                if ((class051Var.getPacket() instanceof PlayerPositionLookS2CPacket) && this.mode.isSelected(VelocityMode.GRIM)) {
                    this.grimTicks = 3;
                }
            }
        });
        register(MovementInputEvent.class, class040Var -> {
            if (isState() && this.mc.isWorldLoaded() && this.mode.isSelected(VelocityMode.JUMP_RESET)) {
                ClientPlayerEntity player= this.mc.getPlayer();
                if (player.hurtTime == 9 && player.isOnGround() && player.isSprinting() && !this.verticalKnockback) {
                    class040Var.setJumping(true);
                }
            }
        });
    }

    @Override
    public void deactivate() {
        this.pendingGrim = false;
        this.grimTicks = 0;
        this.verticalKnockback = false;
        super.deactivate();
    }
}
