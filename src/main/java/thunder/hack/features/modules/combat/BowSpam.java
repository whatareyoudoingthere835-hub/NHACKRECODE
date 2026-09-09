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

public class BowSpam extends Module {
    private final Setting<Integer> charge = new Setting<>("Charge", 5, 4, 20);
    private final Setting<Boolean> onlyWhenHoldingRightClick = new Setting<>("When Holding RMB", false);
    private final Setting<Boolean> spamCrossbows = new Setting<>("Spam Crossbows", true);
    private final Setting<Integer> crossbowDelay = new Setting<>("Crossbow Delay", 10, 0, 20);
    private final Setting<Boolean> searchInventory = new Setting<>("Search Inventory", true);

    private boolean wasBow = false;
    private boolean wasHoldingRightClick = false;
    private int ticks = 0;

    // Для swapBack
    private int previousSlot = -1;
    private boolean didSwap = false;

    public BowSpam() {
        super("BowSpam", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        wasBow = false;
        wasHoldingRightClick = false;
        ticks = 0;
        previousSlot = -1;
        didSwap = false;
    }

    @Override
    public void onDisable() {
        setPressed(false);
        if (didSwap) swapBack();
    }

    @EventHandler
    public void onSync(EventSync event) {
        if (mc.player == null || mc.interactionManager == null) return;

        // SwapBack если в прошлом тике делали swap
        if (didSwap) {
            swapBack();
            didSwap = false;
        }

        // Ищем заряженный арбалет
        int crossbowSlot = searchInventory.getValue()
                ? findItem(this::isCrossbow, 0, 45)
                : findItem(this::isCrossbow, 0, 9);

        if (spamCrossbows.getValue() && crossbowSlot != -1) {
            if (ticks >= crossbowDelay.getValue()) {
                int slot = crossbowSlot;

                // Если арбалет не в хотбаре - свапаем в хотбар
                if (slot >= 9) {
                    // Ищем подходящий слот в хотбаре (пустой, арбалет или стрела)
                    int validSlot = findItem(
                            stack -> stack.isEmpty()
                                    || stack.isOf(Items.CROSSBOW)
                                    || stack.isOf(Items.ARROW),
                            0, 9
                    );
                    if (validSlot == -1) return;

                    // quickSwap: меняем местами слот хотбара и слот инвентаря
                    quickSwap(validSlot, slot);
                    slot = validSlot;
                }

                // swap: временно переключаемся на нужный слот
                swap(slot);

                // Стреляем
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

                // swapBack будет вызван в следующем тике
                didSwap = true;
                ticks = 0;
            } else {
                ticks++;
            }

            return;
        }

        // Обычный лук
        if (!mc.player.getAbilities().creativeMode
                && findItem(stack -> stack.getItem() instanceof ArrowItem, 0, 45) == -1) {
            return;
        }

        if (!onlyWhenHoldingRightClick.getValue() || mc.options.useKey.isPressed()) {
            boolean isBow = mc.player.getMainHandStack().isOf(Items.BOW)
                    || mc.player.getOffHandStack().isOf(Items.BOW);

            if (!isBow && wasBow) setPressed(false);

            wasBow = isBow;
            if (!isBow) return;

            if (mc.player.getItemUseTime() >= charge.getValue()) {
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

    // ========== Аналоги InvUtils из Meteor ==========

    /**
     * Ищет предмет в инвентаре в диапазоне слотов [from, to)
     * Возвращает индекс слота или -1
     */
    private int findItem(java.util.function.Predicate<ItemStack> predicate, int from, int to) {
        for (int i = from; i < to; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (predicate.test(stack)) return i;
        }
        return -1;
    }

    /**
     * Аналог InvUtils.swap() - переключает выбранный слот хотбара
     * Сохраняет предыдущий слот для swapBack
     */
    private void swap(int hotbarSlot) {
        previousSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(hotbarSlot);
        mc.getNetworkHandler().sendPacket(
                new net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket(hotbarSlot)
        );
    }

    private void swapBack() {
        if (previousSlot == -1) return;
        mc.player.getInventory().setSelectedSlot(previousSlot);
        mc.getNetworkHandler().sendPacket(
                new net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket(previousSlot)
        );
        previousSlot = -1;
    }

    /**
     * Аналог InvUtils.quickSwap() - меняет предмет из инвентаря в хотбар
     * hotbarSlot: 0-8, inventorySlot: 9-44
     */
    private void quickSwap(int hotbarSlot, int inventorySlot) {
        // В открытом инвентаре игрока:
        // Слоты хотбара на экране: 36-44 (inventorySlot + 36)
        // Слоты инвентаря на экране: 9-35 (совпадают)
        // SlotActionType.SWAP с номером hotbarSlot меняет местами
        // указанный слот экрана и слот хотбара

        int screenSlot = inventorySlot < 9
                ? inventorySlot + 36  // хотбар -> экранный
                : inventorySlot;       // инвентарь -> экранный (уже правильный)

        mc.interactionManager.clickSlot(
                mc.player.currentScreenHandler.syncId,
                screenSlot,   // экранный слот откуда берём
                hotbarSlot,   // номер слота хотбара (0-8) куда кладём
                SlotActionType.SWAP,
                mc.player
        );
    }

    // ========== Вспомогательные методы ==========

    private void setPressed(boolean pressed) {
        mc.options.useKey.setPressed(pressed);
    }

    private boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(stack);
    }
}