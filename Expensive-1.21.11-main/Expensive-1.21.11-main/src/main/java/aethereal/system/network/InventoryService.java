package aethereal.system.network;
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

import net.minecraft.util.Hand;

public class InventoryService {
    public final ItemInteractionHelper itemInteractor = new ItemInteractionHelper();

    public final SilentSlotManager hotbarSlotSwapper = new SilentSlotManager();

    public final InventoryItemFinder searcher = new InventoryItemFinder();

    public InventoryService() {
        Expensive.INSTANCE.eventDispatcher().register(PlayerTickEvent.class, class130Var -> {
            if (Mc.INSTANCE.isWorldLoaded() && class130Var.isPre() && !CombatPauseManager.INSTANCE.shouldPauseSwaps()) {
                this.hotbarSlotSwapper.update();
            }
        });
    }

    public void addTask(InventoryTask class012Var, Module class605Var) {
        if (Mc.INSTANCE.getPlayer() == null) {
            return;
        }
        SlotSearchResult2 class329VarResult= class012Var.result();
        if (class329VarResult.found()) {
            SwapUtil.swapAndExecute(class329VarResult.slotReference().increasedSlot(), class012Var.rotation(), class012Var.needStop(), () -> {
                PlayerActionUtil.INSTANCE.interactItem(Hand.MAIN_HAND, PlayerRotationManager.INSTANCE.getCurrentRotation(), false);
            });
        }
    }

    public ItemInteractionHelper itemInteractor() {
        return this.itemInteractor;
    }

    public InventoryItemFinder searcher() {
        return this.searcher;
    }

    public SilentSlotManager hotbarSlotSwapper() {
        return this.hotbarSlotSwapper;
    }
}
