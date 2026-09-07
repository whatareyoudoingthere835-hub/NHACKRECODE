package aethereal.features.modules.autobuy;
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


import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Aliases(aliases = {"Auto Buy", "AutoBuy", "Авто скуп", "Авто скупка", "Скупщик", "Auction Buyer", "AH Buy"})
public class AutoBuyModule extends Module {

    public final BooleanSetting autoSell;
    public final BooleanSetting tgNotify;
    public final BooleanSetting autoRefresh;
    public final NumberSetting buyDelay;
    public final TextFieldSetting searchCommand;

    public final Mc mc;
    public final Stopwatch buyTimer = new Stopwatch();
    public final Stopwatch refreshTimer = new Stopwatch();
    public final Stopwatch notifyTimer = new Stopwatch();
    public final TooltipPriceReader priceReader;

    public int totalBought = 0;
    public long totalSpent = 0L;

    public AutoBuyModule() {
        super(ModuleTab.AUTOBUY, "Auto Buy");
        this.mc = Mc.INSTANCE;
        this.priceReader = new TooltipPriceReader();

        this.autoSell = new BooleanSetting(
            Translation.clearText("Авто-перевыставление"),
            Translation.clearText("Автоматически перевыставлять купленные товары на продажу")
        ).setValue(true);

        this.tgNotify = new BooleanSetting(
            Translation.clearText("Telegram Уведомления"),
            Translation.clearText("Отправка уведомлений о покупках в Telegram бот")
        ).setValue(true);

        this.autoRefresh = new BooleanSetting(
            Translation.clearText("Авто-обновление аукциона"),
            Translation.clearText("Периодически нажимать кнопку обновления аукциона")
        ).setValue(true);

        this.buyDelay = new NumberSetting(
            Translation.clearText("Задержка покупки"),
            Translation.clearText("Задержка между кликами выкупа в миллисекундах")
        ).range(50f, 2000f).step(50f).unit(SettingUnit.MILLISECONDS);
        this.buyDelay.setCurrentValue(150f);

        this.searchCommand = new TextFieldSetting(
            Translation.clearText("Команда поиска"),
            Translation.clearText("Команда для открывания аукциона")
        ).setText("ah");

        addSettings(autoSell, tgNotify, autoRefresh, buyDelay, searchCommand);

        // Tick event for scanning open auction screen
        register(PlayerTickEvent.class, event -> {
            if (!isState() || !mc.isWorldLoaded() || !event.isPre()) return;
            ClientPlayerEntity player= mc.getPlayer();
            if (player == null) return;

            scanAuctionScreen(player);
        });

        // Packet receive event for purchase confirmation messages
        register(PacketReceiveEvent.class, event -> {
            if (!isState() || !mc.isWorldLoaded()) return;
            if (event.getPacket() instanceof GameMessageS2CPacket packet) {
                handleChatMessage(packet.content().getString());
            }
        });
    }

    private void scanAuctionScreen(ClientPlayerEntity player) {
        if (!(mc.getCurrentScreen() instanceof GenericContainerScreen containerScreen)) return;
        GenericContainerScreenHandler handler= containerScreen.getScreenHandler();
        String title= containerScreen.getTitle().getString().toLowerCase(Locale.ROOT);

        // Check if we are looking at an auction window
        if (!title.contains("аукцион") && !title.contains("рынок") && !title.contains("auction") && !title.contains("поиск")) {
            return;
        }

        // Check inventory free slots
        if (getEmptyInventorySlots(player) <= 0) {
            if (notifyTimer.hasElapsed(5000)) {
                Expensive.INSTANCE.notificationRepository().post(
                    NotificationType.WARNING,
                    Text.literal("Автоскупка: Инвентарь полон!"),
                    3L,
                    TimeUnit.SECONDS
                );
                notifyTimer.reset();
            }
            return;
        }

        if (!buyTimer.hasElapsed((long) buyDelay.currentValue())) return;

        List<AutoBuyTarget> targets= AutoBuyDataStorage.INSTANCE.getTargets();
        if (targets.isEmpty()) return;

        int slots= handler.getInventory().size();
        boolean boughtItem= false;

        for (int i = 0; i < Math.min(45, slots); i++) {
            var slot= handler.getSlot(i);
            if (!slot.hasStack()) continue;
            ItemStack stack= slot.getStack();

            // Read item price from tooltip
            long price= priceReader.getPrice(stack);
            if (price <= 0) continue;

            // Check if any target matches this item
            for (AutoBuyTarget target : targets) {
                if (target.matches(stack, price)) {
                    // Click slot to buy!
                    PlayerInventoryUtils.INSTANCE.windowClick(SlotActionType.QUICK_MOVE, i, 0, true);
                    buyTimer.reset();
                    refreshTimer.reset();
                    boughtItem = true;

                    totalBought++;
                    totalSpent += price;

                    if (tgNotify.isValue() && notifyTimer.hasElapsed(2000)) {
                        String text= "Куплено: " + stack.getName().getString() + " x" + stack.getCount() + " за " + AutoBuyTarget.money(price);
                        Expensive.INSTANCE.notificationRepository().post(
                            NotificationType.SUCCESS,
                            Text.literal(text),
                            4L,
                            TimeUnit.SECONDS
                        );
                        notifyTimer.reset();
                    }

                    // Auto relist if enabled
                    if (autoSell.isValue() && !target.getSellPriceText().isBlank()) {
                        long sellPrice= AutoBuyTarget.parseMoney(target.getSellPriceText());
                        if (sellPrice > 0) {
                            String cmd= "ah sell " + sellPrice;
                            player.networkHandler.sendChatCommand(cmd);
                        }
                    }
                    return; // 1 buy per cycle
                }
            }
        }

        // Auto refresh auction page if enabled and idle for 2.5s
        if (autoRefresh.isValue() && !boughtItem && refreshTimer.hasElapsed(2500)) {
            // Slot 49 is standard refresh button in auction GUIs
            if (slots >= 49 && handler.getSlot(49).hasStack()) {
                PlayerInventoryUtils.INSTANCE.windowClick(SlotActionType.PICKUP, 49, 0, false);
            }
            refreshTimer.reset();
        }
    }

    private int getEmptyInventorySlots(ClientPlayerEntity player) {
        int count= 0;
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getStack(i).isEmpty()) count++;
        }
        return count;
    }

    private void handleChatMessage(String rawMsg) {
        if (rawMsg == null) return;
        String lower= Formatting.strip(rawMsg).toLowerCase(Locale.ROOT);
        if (lower.contains("успешно куплен") || lower.contains("вы успешно приобрели") || lower.contains("successfully purchased")) {
            if (notifyTimer.hasElapsed(1000)) {
                Expensive.INSTANCE.notificationRepository().post(
                    NotificationType.SUCCESS,
                    Text.literal("Автоскупка: Успешный выкуп товара!"),
                    3L,
                    TimeUnit.SECONDS
                );
                notifyTimer.reset();
            }
        }
    }

    @Override
    public void activate() {
        totalBought = 0;
        totalSpent = 0L;
        buyTimer.reset();
        refreshTimer.reset();
        AutoBuyDataStorage.INSTANCE.load();
        super.activate();
    }
}
