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

import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.BlockPos;

@Aliases(aliases = {"Edge Jump", "Auto Parkour"})
public class EdgeJumpModule extends Module {
    final Mc mc;

    public EdgeJumpModule() {
        super(ModuleTab.MOVEMENT, "Edge Jump");
        this.mc = Mc.INSTANCE;
        register(MovementInputEvent.class, class040Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                ClientPlayerEntity player= this.mc.getPlayer();
                GameOptions gameOptions= this.mc.getGameOptions();
                SimulatedPlayer class136VarSimulateLocalPlayer= SimulatedPlayer.simulateLocalPlayer(1);
                if (class136VarSimulateLocalPlayer.pos.subtract(player.getEntityPos()).normalize().dotProduct(player.getRotationVector().normalize()) > 0.0d && player.isOnGround() && !player.isSneaking() && !gameOptions.sneakKey.isPressed() && !gameOptions.jumpKey.isPressed() && (!isOnSlimeBlock(player) ? class136VarSimulateLocalPlayer.onGround : this.mc.getWorld().getBlockState(BlockPos.ofFloored(class136VarSimulateLocalPlayer.pos).down()).isSolid())) {
                    class040Var.setJumping(true);
                }
            }
        });
    }

    public boolean isOnSlimeBlock(ClientPlayerEntity clientPlayerEntity) {
        return clientPlayerEntity.getEntityWorld().getBlockState(BlockPos.ofFloored(clientPlayerEntity.getX(), clientPlayerEntity.getY() - 0.1d, clientPlayerEntity.getZ())).getBlock() == Blocks.SLIME_BLOCK;
    }
}
