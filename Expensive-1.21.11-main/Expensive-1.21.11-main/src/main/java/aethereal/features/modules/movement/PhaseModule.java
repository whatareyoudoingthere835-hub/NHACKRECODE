package aethereal.features.modules.movement;
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
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class PhaseModule extends Module {
    public final Mc mc;
    public final List<Packet<?>> heldPackets;
    public final ActionScheduler scheduler;
    public Box startBox;
    public int tickCounter;

    public PhaseModule() {
        super(ModuleTab.MOVEMENT, "Phase");
        this.mc = Mc.INSTANCE;
        this.heldPackets = new CopyOnWriteArrayList();
        this.scheduler = new ActionScheduler();
        this.tickCounter = 0;
        register(PacketSendEvent.class, class037Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                this.heldPackets.add(class037Var.getPacket());
                class037Var.cancel();
            }
        });
        register(PlayerTickEvent.class, class130Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                ClientPlayerEntity player= this.mc.getPlayer();
                ClientWorld world= this.mc.getWorld();
                this.tickCounter++;
                this.scheduler.update();
                if (BlockPos.stream(player.getBoundingBox().expand(-0.001d)).noneMatch(blockPos -> {
                    return world.getBlockState(blockPos).isSolid();
                }) && this.tickCounter > 5 && this.scheduler.isFinished()) {
                    for (KeyBinding keyBinding : MovementInputHelper.getMovementKeys(false, false)) {
                        keyBinding.setPressed(false);
                    }
                    this.scheduler.cleanup().addTickStep(0, () -> {
                        setState(false);
                    });
                }
            }
        });
        register(BlockCollisionEvent.class, class114Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                ClientPlayerEntity player= this.mc.getPlayer();
                if (class114Var.getPos().getY() >= player.getBlockY() || player.isSneaking()) {
                    class114Var.setState(Blocks.AIR.getDefaultState());
                }
            }
        });
        register(WorldRenderEvent.class, class016Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                ShapeRenderer.INSTANCE.addBox(class016Var.matrixStack().peek().getPositionMatrix(), this.startBox, -1);
            }
        });
    }

    @Override
    public void activate() {
        if (this.mc.isWorldLoaded()) {
            this.startBox = this.mc.getPlayer().getBoundingBox();
        }
        this.heldPackets.clear();
    }

    @Override
    public void deactivate() {
        if (this.mc.isWorldLoaded()) {
            ClientPlayerEntity player= this.mc.getPlayer();
            for (KeyBinding keyBinding : MovementInputHelper.getMovementKeys(false, false)) {
                keyBinding.setPressed(InputUtil.isKeyPressed(this.mc.getWindow(), keyBinding.getDefaultKey().getCode()));
            }
            this.heldPackets.forEach(PacketSender::sendPacket);
            PacketSender.sendPacket(new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getY(), player.getYaw(), player.getPitch(), player.isOnGround(), player.horizontalCollision));
        }
        this.heldPackets.clear();
        this.tickCounter = 0;
    }
}
