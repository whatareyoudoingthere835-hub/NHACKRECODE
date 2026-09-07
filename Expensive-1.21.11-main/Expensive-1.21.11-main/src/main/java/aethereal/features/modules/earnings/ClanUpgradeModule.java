package aethereal.features.modules.earnings;
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
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Aliases(aliases = {"Clan Upgrade", "Auto Clan Upgrade", "Clan Upgrader", "Прокачка клана"})
public class ClanUpgradeModule extends Module {
    public final Mc mc;
    private int pendingSlot = -1;
    private boolean waitingForSwap = false;

    public ClanUpgradeModule() {
        super(ModuleTab.EARNINGS, "Clan Upgrade");
        this.mc = Mc.INSTANCE;

        register(PlayerTickEvent.class, event -> {
            if (!isState() || !this.mc.isWorldLoaded() || !event.isPre()) {
                return;
            }
            ClientPlayerEntity player= this.mc.getPlayer();
            if (player == null) {
                return;
            }

            tickClanUpgrade(player);
        });
    }

    private void tickClanUpgrade(ClientPlayerEntity player) {
        int slot= findItemSlot(player);
        if (slot == -1) {
            return;
        }

        if (waitingForSwap) {
            waitingForSwap = false;
            BlockPos targetPos= player.getBlockPos().down();
            if (this.mc.getWorld().getBlockState(targetPos).isSolid()) {
                this.mc.getInteractionManager().interactBlock(player, Hand.MAIN_HAND,
                    new BlockHitResult(targetPos.toCenterPos(), Direction.UP, targetPos, false));
            }
            return;
        }

        pendingSlot = slot;
        Expensive.INSTANCE.inventoryService().hotbarSlotSwapper().swapTo(this, new InventorySlotRef(slot, InventoryScope.HOTBAR), 0);
        waitingForSwap = true;
    }

    private int findItemSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            var stack= player.getInventory().getStack(i);
            if (stack.isOf(Items.GOLD_INGOT) || stack.isOf(Items.DIAMOND) || stack.isOf(Items.EMERALD)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void deactivate() {
        this.pendingSlot = -1;
        this.waitingForSwap = false;
        super.deactivate();
    }
}
