package thunder.hack.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import thunder.hack.events.impl.EventSync;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class InstantCrossbow extends Module {
    private final Setting<Float> charge = new Setting<>("Charge", 5f, 4f, 20f);
    private final Setting<Boolean> onlyWhenHoldingRightClick = new Setting<>("When Holding RMB", false);
    private final Setting<Boolean> spamCrossbows = new Setting<>("Spam Crossbows", true);
    private final Setting<Float> crossbowDelay = new Setting<>("Crossbow Delay", 10f, 0f, 20f);
    private final Setting<Boolean> searchInventory = new Setting<>("Search Inventory", true);

    private boolean wasBow = false;
    private boolean wasHoldingRightClick = false;
    private int ticks = 0;

    public InstantCrossbow() {
        super("crossbow exploit", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        wasBow = false;
        wasHoldingRightClick = false;
        ticks = 0;
    }

    @Override
    public void onDisable() {
        setPressed(false);
    }

    @EventHandler
    public void onSync(EventSync event) {
        if (mc.player == null || mc.interactionManager == null) return;

        if (spamCrossbows.getValue()) {
            handleCrossbowSpam();
            return;
        }

        handleBowSpam();
    }

    private void handleCrossbowSpam() {
        int crossbowSlot = searchInventory.getValue()
                ? findCrossbowInInventory()
                : findCrossbowInHotbar();

        if (crossbowSlot == -1) return;

        if (ticks < crossbowDelay.getValue().intValue()) {
            ticks++;
            return;
        }

        int screenSlot = crossbowSlot;

        // Если арбалет в основном инвентаре - перемещаем в хотбар
        if (crossbowSlot >= 9) {
            int validHotbarSlot = findValidHotbarSlot();
            if (validHotbarSlot == -1) return;

            moveItemToHotbar(crossbowSlot, validHotbarSlot);
            screenSlot = validHotbarSlot;
        }

        // Выбираем слот и стреляем
        int prevSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(screenSlot);

        // Небольшая задержка чтобы сервер успел обновить выбранный слот
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

        mc.player.getInventory().setSelectedSlot(prevSlot);
        ticks = 0;
    }

    private void handleBowSpam() {
        if (!mc.player.getAbilities().creativeMode && !hasArrows()) return;

        if (!onlyWhenHoldingRightClick.getValue() || mc.options.useKey.isPressed()) {
            boolean isBow = mc.player.getMainHandStack().getItem() == Items.BOW ||
                    mc.player.getOffHandStack().getItem() == Items.BOW;

            if (!isBow && wasBow) setPressed(false);
            wasBow = isBow;
            if (!isBow) return;

            if (mc.player.getItemUseTime() >= charge.getValue().intValue()) {
                mc.interactionManager.stopUsingItem(mc.player);
            } else {
                setPressed(true);
            }

            wasHoldingRightClick = mc.options.useKey.isPressed();
        } else {
            if (wasHoldingRightClick) {
                setPressed(false);
                wasHoldingRightClick = false;
            }
        }
    }

    private void setPressed(boolean pressed) {
        mc.options.useKey.setPressed(pressed);
    }

    private int findCrossbowInHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isCrossbow(stack)) return i;
        }
        return -1;
    }

    private int findCrossbowInInventory() {
        // Сначала ищем в хотбаре
        int hotbarSlot = findCrossbowInHotbar();
        if (hotbarSlot != -1) return hotbarSlot;

        // Потом в основном инвентаре
        for (int i = 9; i < mc.player.getInventory().getMainStacks().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isCrossbow(stack)) return i;
        }
        return -1;
    }

    private int findValidHotbarSlot() {
        // Приоритет: пустые слоты
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) return i;
        }
        return -1;
    }

    private boolean hasArrows() {
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof ArrowItem) return true;
        }
        return false;
    }

    private boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack);
    }

    private void moveItemToHotbar(int inventorySlot, int hotbarSlot) {
        if (mc.player == null || mc.interactionManager == null) return;

        // В экране инвентаря:
        // Основной инвентарь: слоты 9-35 (совпадают с индексами)
        // Хотбар: слоты 36-44 (индекс + 36)
        // Используем SWAP для прямого обмена без промежуточных кликов
        mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                inventorySlot, // экранный слот основного инвентаря (9-35)
                hotbarSlot,    // номер слота хотбара (0-8) для SlotActionType.SWAP
                SlotActionType.SWAP,
                mc.player
        );
    }
}