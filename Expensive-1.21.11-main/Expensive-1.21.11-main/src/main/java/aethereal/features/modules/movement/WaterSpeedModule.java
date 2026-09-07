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

import java.util.concurrent.TimeUnit;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;

@Aliases(aliases = {"Water Speed", "Fast Swim", "Water Movement", "Swim Boost", "Aqua Speed", "Fast Water Travel", "Water Sprint", "Speed in Water"})
public class WaterSpeedModule extends Module {
    public WaterSpeedModule() {
        super(ModuleTab.MOVEMENT, "Water Speed");
        Stopwatch class314Var= new Stopwatch();
        Mc class815Var= Mc.INSTANCE;
        register(PlayerTickEvent.class, class130Var -> {
            float f;
            if (isState() && class815Var.isWorldLoaded() && class130Var.isPre()) {
                ClientPlayerEntity player= class815Var.getPlayer();
                ClientWorld world= class815Var.getWorld();
                GameOptions gameOptions= class815Var.getGameOptions();
                if (SlotSyncHandler.lastSprinting) {
                    return;
                }
                if ((world.getBlockState(new BlockPos((int) player.getEntityPos().x, (int) (player.getEntityPos().y + 0.2d), (int) player.getEntityPos().z)).getBlock() != Blocks.WATER && gameOptions.jumpKey.isPressed()) || !player.isTouchingWater()) {
                    class314Var.reset();
                }
                if (player.isTouchingWater() && class314Var.hasElapsed(160L, TimeUnit.MILLISECONDS)) {
                    if (gameOptions.jumpKey.isPressed()) {
                        f = 0.05f;
                    } else if (gameOptions.sneakKey.isPressed()) {
                        f = -0.05f;
                    } else {
                        f = !player.isSprinting() ? 0.005f : 0.0f;
                    }
                    float f2= f;
                    PlayerInput input= player.input.playerInput;
                    PacketSender.sendPacket(new PlayerInputC2SPacket(new PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), true, input.sprint())));
                    PacketSender.sendPacket(new PlayerInputC2SPacket(new PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), false, input.sprint())));
                    float f3= player.isSprinting() ? 1.025f : 1.156f;
                    player.setVelocity(player.getVelocity().x * ((double) f3), player.getVelocity().y + ((double) f2), player.getVelocity().z * ((double) f3));
                }
            }
        });
    }
}
