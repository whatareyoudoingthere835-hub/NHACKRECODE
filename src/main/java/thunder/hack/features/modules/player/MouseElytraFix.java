package thunder.hack.features.modules.player;

import thunder.hack.utility.player.ItemChecks;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import thunder.hack.features.modules.Module;
import thunder.hack.utility.Timer;

public class MouseElytraFix extends Module {
    public MouseElytraFix() {
        super("MouseElytraFix", Category.PLAYER);
    }

    private final Timer delay = new Timer();

    @Override
    public void onUpdate() {
        ItemStack cursorStack = mc.player.currentScreenHandler.getCursorStack();
        if (ItemChecks.armorSlot(cursorStack) == EquipmentSlot.CHEST && !ItemChecks.isElytra(cursorStack) && !ElytraSwap.swapping) {
            if (delay.every(300))
                if (mc.player.getInventory().getStack(38).getItem() == Items.ELYTRA) {
                    mc.interactionManager.clickSlot(0, 6, 1, SlotActionType.PICKUP, mc.player);
                    int empty = findEmptySlot();
                    boolean needDrop = (empty == 999);
                    if (needDrop)
                        empty = 9;
                    mc.interactionManager.clickSlot(0, empty, 1, SlotActionType.PICKUP, mc.player);
                    if (needDrop)
                        mc.interactionManager.clickSlot(0, -999, 1, SlotActionType.PICKUP, mc.player);
                }
        }
    }

    public static int findEmptySlot() {
        for (int i = 0; i < 36; i++)
            if (mc.player.getInventory().getStack(i).isEmpty()) return i < 9 ? i + 36 : i;
        return 999;
    }
}
