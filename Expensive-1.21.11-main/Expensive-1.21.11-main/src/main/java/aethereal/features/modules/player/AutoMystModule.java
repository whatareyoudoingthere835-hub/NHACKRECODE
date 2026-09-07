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

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class AutoMystModule extends Module {
    final Mc mc;
    public BlockPos enderChestPos;
    public long lastInteractTime;
    public boolean looting;

    public AutoMystModule() {
        super(ModuleTab.PLAYER, "Auto Myst");
        this.mc = Mc.INSTANCE;
        register(InteractBlockEvent.class, class217Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                BlockHitResult result= class217Var.getResult();
                if (this.mc.getWorld().getBlockState(result.getBlockPos()).isOf(Blocks.ENDER_CHEST)) {
                    this.enderChestPos = result.getBlockPos().toImmutable();
                    this.lastInteractTime = System.currentTimeMillis();
                }
            }
        });
        register(PlayerTickEvent.class, class130Var -> {
            if (isState() && this.mc.isWorldLoaded()) {
                ClientPlayerEntity player= this.mc.getPlayer();
                GenericContainerScreen currentScreen= (GenericContainerScreen) (this.mc.getCurrentScreen());
                if (!(currentScreen instanceof GenericContainerScreen)) {
                    if (this.looting) {
                        this.looting = false;
                        return;
                    }
                    return;
                }
                GenericContainerScreenHandler genericContainerScreenHandler= (GenericContainerScreenHandler) currentScreen.getScreenHandler();
                if (System.currentTimeMillis() - this.lastInteractTime < 1000) {
                    this.looting = true;
                }
                if (!this.looting || player.getItemCooldownManager().isCoolingDown(Items.GUNPOWDER.getDefaultStack())) {
                    return;
                }
                takeAllItems(genericContainerScreenHandler);
            }
        });
    }

    @Override
    public void deactivate() {
        this.looting = false;
        super.deactivate();
    }

    public void takeAllItems(GenericContainerScreenHandler genericContainerScreenHandler) {
        for (int i = 0; i < genericContainerScreenHandler.getInventory().size(); i++) {
            if (genericContainerScreenHandler.getSlot(i).hasStack()) {
                PlayerInventoryUtils.INSTANCE.windowClick(SlotActionType.QUICK_MOVE, i, 0, true);
            }
        }
    }
}
