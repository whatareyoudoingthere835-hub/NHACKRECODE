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
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;

@Aliases(aliases = {"Auto Crafter", "Auto Craft", "Craft Bot", "Авто крафтер"})
public class AutoCrafterModule extends Module {
    public final BooleanSetting autoSell;
    public final BooleanSetting takeMaterials;
    public final Mc mc;
    private final Stopwatch timer = new Stopwatch();

    public AutoCrafterModule() {
        super(ModuleTab.EARNINGS, "Auto Crafter");
        this.autoSell = new BooleanSetting(Translation.clearText("Авто продажа"), Translation.clearText("Автоматически продавать созданный предмет"));
        this.takeMaterials = new BooleanSetting(Translation.clearText("Забирать материалы"), Translation.clearText("Автоматически забирать материалы из сундука"));
        this.mc = Mc.INSTANCE;

        addSettings(this.autoSell, this.takeMaterials);

        register(PlayerTickEvent.class, event -> {
            if (!isState() || !this.mc.isWorldLoaded() || !event.isPre()) {
                return;
            }
            ClientPlayerEntity player = this.mc.getPlayer();
            if (player == null) {
                return;
            }

            if (!this.timer.hasElapsed(100L)) {
                return;
            }

            if (player.currentScreenHandler instanceof CraftingScreenHandler craftHandler) {
                if (craftHandler.getSlot(0).hasStack()) {
                    this.mc.getInteractionManager().clickSlot(craftHandler.syncId, 0, 0, SlotActionType.QUICK_MOVE, player);
                    this.timer.reset();
                }
            } else if (this.takeMaterials.isValue() && player.currentScreenHandler instanceof GenericContainerScreenHandler containerHandler) {
                int containerSize = containerHandler.getRows() * 9;
                for (int i = 0; i < containerSize; i++) {
                    Slot slot = containerHandler.getSlot(i);
                    if (slot != null && slot.hasStack()) {
                        ItemStack stack = slot.getStack();
                        if (!stack.isEmpty()) {
                            this.mc.getInteractionManager().clickSlot(containerHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, player);
                            this.timer.reset();
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }
}
