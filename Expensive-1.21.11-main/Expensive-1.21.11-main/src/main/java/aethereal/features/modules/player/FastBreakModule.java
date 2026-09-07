package aethereal.features.modules.player;
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

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;

@Aliases(aliases = {"Fast Break", "Quick Break", "Fast Mine", "Speed Mine", "Quick Mine", "Instant Break", "Instant Mine", "Fast Dig"})
public class FastBreakModule extends Module {
    public FastBreakModule() {
        super(ModuleTab.PLAYER, "Fast Break");
        register(PlayerTickEvent.class, class130Var -> {
            Mc class815Var= Mc.INSTANCE;
            if (isState() && class815Var.isWorldLoaded() && class130Var.isPre()) {
                ClientPlayerInteractionManager interactionManager= class815Var.getInteractionManager();
                BlockHitResult crosshairTarget= (BlockHitResult) (class815Var.getCrosshairTarget());
                float f= interactionManager.currentBreakingProgress;
                if (crosshairTarget instanceof BlockHitResult) {
                    BlockHitResult blockHitResult= crosshairTarget;
                    if (f > 0.3d) {
                        PacketSender.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockHitResult.getBlockPos(), blockHitResult.getSide()));
                    }
                }
            }
        });
    }
}
