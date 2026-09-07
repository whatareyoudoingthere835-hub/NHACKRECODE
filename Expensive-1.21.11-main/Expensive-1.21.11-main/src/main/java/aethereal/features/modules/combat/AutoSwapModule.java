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

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;

@Aliases(aliases = {"Swap", "Auto Swap", "Fast Swap", "Quick Swap", "Hotkey Swap", "Inventory Swap", "Offhand Swap", "Auto Item Switch", "Switch Items", "Auto Offhand", "Quick Switch", "Item Swap", "Swap Items", "Auto Hand Swap", "Key Swap"})
public class AutoSwapModule extends Module {
    public final ModeSetting<AutoSwapItemType> swapFrom;
    public final ModeSetting<AutoSwapItemType> swapTo;
    public final Mc mc;

    public AutoSwapModule() {
        super(ModuleTab.COMBAT, "Auto Swap");
        this.swapFrom = new ModeSetting(Lang.COMBAT_AUTOSWAP_SWAPFROM).values(AutoSwapItemType.class);
        this.swapTo = new ModeSetting(Lang.COMBAT_AUTOSWAP_SWAPTO).values(AutoSwapItemType.class);
        this.mc = Mc.INSTANCE;
        KeybindSetting class663Var= new KeybindSetting(Lang.COMBAT_AUTOSWAP_SWAPKEY);
        addSettings(this.swapFrom, this.swapTo, class663Var);
        class663Var.consumer(class664Var -> {
            swap();
        });
    }

    public void swap() {
        if (isState() && this.mc.isWorldLoaded() && !CombatPauseManager.INSTANCE.shouldPauseAutoSwap()) {
            ItemMatcher class856VarMethod005= resolveItemMatcher((AutoSwapItemType) this.swapFrom.currentValue(), (AutoSwapItemType) this.swapTo.currentValue());
            Expensive.INSTANCE.inventoryService().searcher().findAllItems(class856VarMethod005.predicate(), InventoryScope.HOTBAR, InventoryScope.INVENTORY).stream().min(Comparator.comparingInt((SlotSearchResult2 class329Var) -> {
                FoodComponent foodComponent= (FoodComponent) class329Var.stack().get(DataComponentTypes.FOOD);
                if (foodComponent != null) {
                    return (int) (-(foodComponent.nutrition() * foodComponent.saturation()));
                }
                return 0;
            }).thenComparing((SlotSearchResult2 class329Var2) -> {
                return Boolean.valueOf(!class329Var2.stack().hasEnchantments());
            })).ifPresentOrElse(this::swapItemToOffhand, () -> {
                notifyItemNotFound(class856VarMethod005.displayItem());
            });
        }
    }

    public void swapItemToOffhand(SlotSearchResult2 class329Var) {
        if (GrimDelayHandler.script.isFinished()) {
            SwapUtil.swapToOffhand(class329Var.slotReference().increasedSlot());
            Expensive.INSTANCE.notificationRepository().post(Text.empty().append(Lang.ELYTRAHELPER_SWAPPED_TO.effective().replace("{item}", String.valueOf(Formatting.RED) + StringHelper.stripTextFormat(class329Var.stack().getName().getString()).replace("[★]", "").replace("fff", "").replace("ggg", "").replace("xxx", "").trim() + String.valueOf(Formatting.RESET))), class329Var.stack(), 3L, TimeUnit.SECONDS);
        }
    }

    public static void notifyItemNotFound(Item item) {
        Expensive.INSTANCE.notificationRepository().post(NotificationType.ERROR, (Text) Text.literal("В Вашем инвентаре не найден предмет %s.".formatted(String.valueOf(Formatting.RED) + item.getName().getString())), 3L, TimeUnit.SECONDS);
    }

    public ItemMatcher resolveItemMatcher(AutoSwapItemType class855Var, AutoSwapItemType class855Var2) {
        return class855Var.filter().test(Mc.INSTANCE.getPlayer().getOffHandStack()) ? new ItemMatcher(class855Var2.filter(), class855Var2.displayItem()) : new ItemMatcher(class855Var.filter(), class855Var.displayItem());
    }
}
