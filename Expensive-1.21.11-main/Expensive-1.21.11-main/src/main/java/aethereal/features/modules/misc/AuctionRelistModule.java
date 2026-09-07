package aethereal.features.modules.misc;
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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;

public class AuctionRelistModule extends Module {
    static final int maxAttempts = 3;
    static final long actionTimeoutMs = 2000;
    static final long sellTimeoutMs = 5000;
    static final long slotClickDelayMs = 100;
    static final long closeDelayMs = 300;
    static final long emptyStorageTimeoutMs = 1000;
    public final NumberSetting cycleTimeSetting;

    public final Stopwatch actionStopwatch;

    public final Stopwatch cycleStopwatch;

    public final Stopwatch storageStopwatch;
    public final Deque<PricedItemStack> relistQueue;

    public final TooltipPriceReader priceReader;
    public AuctionState currentState;

    public PricedItemStack currentItem;
    public int attemptCount;
    public boolean selling;
    public boolean storageOpened;
    public boolean itemFound;
    public ItemStack lastProcessedStack;

    public AuctionRelistModule() {
        super(ModuleTab.MISC, "Auction Relist");
        this.cycleTimeSetting = new NumberSetting(Lang.AUCTION_RELIST_CYCLE_TIME, Lang.AUCTION_RELIST_CYCLE_TIME_DESC).currentValue(10.0f).step(1.0f).range(1.0f, 60.0f).unit(SettingUnit.SECONDS);
        this.actionStopwatch = new Stopwatch();
        this.cycleStopwatch = new Stopwatch();
        this.storageStopwatch = new Stopwatch();
        this.relistQueue = new ArrayDeque();
        this.priceReader = new TooltipPriceReader();
        this.currentState = AuctionState.IDLE;
        this.currentItem = null;
        this.attemptCount = 0;
        this.selling = false;
        this.storageOpened = false;
        this.itemFound = false;
        this.lastProcessedStack = ItemStack.EMPTY;
        addSettings(this.cycleTimeSetting);
        register(PacketReceiveEvent.class, class051Var -> {
            if ((class051Var.getPacket()) instanceof GameMessageS2CPacket packet ) {
                String lowerCase= StringHelper.stripTextFormat(packet.content().getString()).toLowerCase();
                if (lowerCase.contains("Ð²Ñ‹ÑÑ‚Ð°Ð²Ð»ÐµÐ½ Ð½Ð° Ð¿Ñ€Ð¾Ð´Ð°Ð¶Ñƒ")) {
                    this.selling = false;
                    this.currentItem = null;
                }
                if (lowerCase.contains("Ð²Ñ‹ Ð½Ðµ Ð¼Ð¾Ð¶ÐµÑ‚Ðµ Ð¿Ñ€Ð¾Ð´Ð°Ñ‚ÑŒ Ð²Ð¾Ð·Ð´ÑƒÑ…")) {
                    this.selling = false;
                }
                if (lowerCase.contains("ÑÐ»Ð¸ÑˆÐºÐ¾Ð¼ Ð´ÐµÑˆÐµÐ²Ð¾") || lowerCase.contains("ÑÐ»Ð¸ÑˆÐºÐ¾Ð¼ Ð´Ð¾Ñ€Ð¾Ð³Ð¾")) {
                    resendSellCommand();
                }
                if (lowerCase.contains("Ð¿Ð¾ÑÐ»Ðµ Ð²Ñ…Ð¾Ð´Ð° Ð½Ð° Ñ€ÐµÐ¶Ð¸Ð¼ Ð½ÐµÐ¾Ð±Ñ…Ð¾Ð´Ð¸Ð¼Ð¾ Ð½ÐµÐ¼Ð½Ð¾Ð³Ð¾ Ð¿Ð¾Ð´Ð¾Ð¶Ð´Ð°Ñ‚ÑŒ Ð¿ÐµÑ€ÐµÐ´ Ð¸ÑÐ¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ð½Ð¸ÐµÐ¼ Ð°ÑƒÐºÑ†Ð¸Ð¾Ð½Ð°. Ð¿Ð¾Ð´Ð¾Ð¶Ð´Ð¸Ñ‚Ðµ")) {
                    setState(false);
                }
            }
        });
        register(PlayerTickEvent.class, class130Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded() && isOnFuntimeAnarchy()) {
                ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
                Screen currentScreen= Mc.INSTANCE.getCurrentScreen();
                switch (this.currentState.ordinal()) {
                    case 0:
                        handleIdle();
                        break;
                    case 1:
                        handleOpenAuction(player);
                        break;
                    case 2:
                        handleClickStorageTab(player, currentScreen);
                        break;
                    case maxAttempts:
                        handleCollectItems(player, currentScreen);
                        break;
                    case 4:
                        handleRelistItems(player, currentScreen);
                        break;
                }
            }
        });
    }

    public void resendSellCommand() {
        ClientPlayerEntity player;
        if (this.currentItem == null || (player = Mc.INSTANCE.getPlayer()) == null) {
            return;
        }
        player.networkHandler.sendChatCommand("ah sell " + this.currentItem.price());
    }

    public boolean isOnFuntimeAnarchy() {
        if (ServerUtil.isConnectedToServer("funtime") && ServerUtil.getAnarchy() != -1) {
            return true;
        }
        notifyError("[Auction Relist] Ð’Ñ‹ Ð´Ð¾Ð»Ð¶Ð½Ñ‹ Ð±Ñ‹Ñ‚ÑŒ Ð¿Ð¾Ð´ÐºÐ»ÑŽÑ‡ÐµÐ½Ñ‹ Ðº " + String.valueOf(Formatting.RED) + "FunTime!");
        setState(false);
        return false;
    }

    public void handleIdle() {
        if (this.cycleStopwatch.hasElapsed((long) this.cycleTimeSetting.currentValue(), TimeUnit.SECONDS)) {
            this.cycleStopwatch.reset();
            setAuctionState(AuctionState.OPEN_AH);
        }
    }

    public void handleOpenAuction(ClientPlayerEntity clientPlayerEntity) {
        clientPlayerEntity.networkHandler.sendChatCommand("ah");
        setAuctionState(AuctionState.CLICK_STORAGE_TAB);
    }

    public void handleClickStorageTab(ClientPlayerEntity clientPlayerEntity, Screen screen) {
        if (screen instanceof GenericContainerScreen) {
            GenericContainerScreen genericContainerScreen= (GenericContainerScreen) screen;
            if (genericContainerScreen.getTitle().getString().toLowerCase().contains("Ð°ÑƒÐºÑ†Ð¸Ð¾Ð½")) {
                if (MovementInputHelper.hasPlayerMovement()) {
                    return;
                }
                PlayerActionUtil.INSTANCE.clickSlot(genericContainerScreen.getScreenHandler().syncId, 46, 0, SlotActionType.QUICK_MOVE, true);
                resetStorageState();
                setAuctionState(AuctionState.COLLECT_ITEMS);
                return;
            }
        }
        if (this.actionStopwatch.hasElapsed(actionTimeoutMs)) {
            retryOpenAuction();
        }
    }

    public void handleCollectItems(ClientPlayerEntity clientPlayerEntity, Screen screen) {
        if (screen instanceof GenericContainerScreen) {
            GenericContainerScreen genericContainerScreen= (GenericContainerScreen) screen;
            if (genericContainerScreen.getTitle().getString().toLowerCase().contains("Ñ…Ñ€Ð°Ð½Ð¸Ð»Ð¸Ñ‰Ðµ")) {
                if (!this.storageOpened) {
                    this.storageStopwatch.reset();
                    this.storageOpened = true;
                    this.lastProcessedStack = ItemStack.EMPTY;
                }
                Slot slot= genericContainerScreen.getScreenHandler().getSlot(0);
                if (slot.hasStack() && this.actionStopwatch.hasElapsed(slotClickDelayMs)) {
                    processStorageItem(genericContainerScreen, slot.getStack());
                    return;
                }
                if (this.itemFound && this.actionStopwatch.hasElapsed(closeDelayMs)) {
                    clientPlayerEntity.closeScreen();
                    setAuctionState(AuctionState.RELIST_ITEMS);
                    return;
                } else {
                    if (!this.itemFound && this.storageOpened && this.storageStopwatch.hasElapsed(emptyStorageTimeoutMs)) {
                        clientPlayerEntity.closeScreen();
                        notifyError("[Auction Relist] ÐÐµ Ð½Ð°Ð¹Ð´ÐµÐ½Ð¾ Ð¿Ñ€ÐµÐ´Ð¼ÐµÑ‚Ð¾Ð² Ð´Ð»Ñ Ñ€ÐµÐ»Ð¸ÑÑ‚Ð¸Ð½Ð³Ð°!");
                        setAuctionState(AuctionState.IDLE);
                        return;
                    }
                    return;
                }
            }
        }
        this.storageOpened = false;
        if (this.actionStopwatch.hasElapsed(actionTimeoutMs)) {
            setAuctionState(AuctionState.CLICK_STORAGE_TAB);
        }
    }

    public void processStorageItem(GenericContainerScreen genericContainerScreen, ItemStack itemStack) {
        if (this.lastProcessedStack.isEmpty() || !ItemStack.areItemsAndComponentsEqual(itemStack, this.lastProcessedStack)) {
            ItemStack itemStackCopy= itemStack.copy();
            int price= this.priceReader.getPrice(itemStackCopy);
            this.lastProcessedStack = itemStackCopy;
            if (price > 0) {
                this.relistQueue.addLast(new PricedItemStack(price, itemStackCopy));
            } else {
                notifyError("[Auction Relist] ÐÐµÐºÐ¾Ñ€Ñ€ÐµÐºÑ‚Ð½Ð°Ñ Ñ†ÐµÐ½Ð° Ñƒ Ð¿Ñ€ÐµÐ´Ð¼ÐµÑ‚Ð°, Ð¿Ñ€Ð¾Ð¿ÑƒÑÐºÐ°ÑŽ.");
            }
            PlayerActionUtil.INSTANCE.clickSlot(genericContainerScreen.getScreenHandler().syncId, 0, 0, SlotActionType.QUICK_MOVE, true);
            this.actionStopwatch.reset();
            this.itemFound = true;
        }
    }

    public void handleRelistItems(ClientPlayerEntity clientPlayerEntity, Screen screen) {
        if (this.selling) {
            if (this.actionStopwatch.hasElapsed(sellTimeoutMs)) {
                handleSellTimeout();
            }
        } else {
            if (this.relistQueue.isEmpty() && this.currentItem == null) {
                setAuctionState(AuctionState.IDLE);
                return;
            }
            if (screen != null) {
                clientPlayerEntity.closeScreen();
                return;
            }
            if (this.currentItem == null) {
                this.currentItem = this.relistQueue.pollFirst();
                if (this.currentItem == null) {
                    setAuctionState(AuctionState.IDLE);
                    return;
                }
            }
            sellCurrentItem(clientPlayerEntity);
        }
    }

    public void handleSellTimeout() {
        this.selling = false;
        if (this.currentItem != null) {
            this.relistQueue.addFirst(this.currentItem);
            this.currentItem = null;
            this.attemptCount++;
            if (this.attemptCount > maxAttempts) {
                notifyError("[Auction Relist] ÐŸÑ€ÐµÐ²Ñ‹ÑˆÐµÐ½Ð¾ ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑÑ‚Ð²Ð¾ Ð¿Ð¾Ð¿Ñ‹Ñ‚Ð¾Ðº, Ð¾ÑÑ‚Ð°Ð½Ð°Ð²Ð»Ð¸Ð²Ð°ÑŽÑÑŒ.");
                setState(false);
            }
        }
    }

    public void sellCurrentItem(ClientPlayerEntity clientPlayerEntity) {
        Optional<SlotSearchResult2> optionalFindItem= Expensive.INSTANCE.inventoryService().searcher().findItem(itemStack -> {
            return !itemStack.isEmpty() && ItemStack.areItemsEqual(itemStack, this.currentItem.stack());
        }, InventoryScope.HOTBAR, InventoryScope.INVENTORY);
        if (optionalFindItem.isEmpty()) {
            this.currentItem = null;
            return;
        }
        if (GrimDelayHandler.script.isFinished()) {
            SlotSearchResult2 class329Var= optionalFindItem.get();
            PricedItemStack class473Var= this.currentItem;
            this.selling = true;
            this.actionStopwatch.reset();
            GrimDelayHandler.script.addTickStep(0, GrimDelayHandler::disableMoveKeys).addTickStep(1, () -> {
                PlayerActionUtil.INSTANCE.clickSlot(clientPlayerEntity.currentScreenHandler.syncId, class329Var.slotReference().increasedSlot(), clientPlayerEntity.getInventory().getSelectedSlot(), SlotActionType.SWAP, true);
                PlayerActionUtil.INSTANCE.updateSlots(true);
            }).addTickStep(2, () -> {
                clientPlayerEntity.networkHandler.sendChatCommand("ah sell " + class473Var.price());
                GrimDelayHandler.enableMoveKeys();
            });
        }
    }

    public void setAuctionState(AuctionState class474Var) {
        this.currentState = class474Var;
        this.actionStopwatch.reset();
        if (class474Var == AuctionState.IDLE) {
            this.cycleStopwatch.reset();
            this.attemptCount = 0;
        }
    }

    public void resetStorageState() {
        this.storageOpened = false;
        this.itemFound = false;
        this.lastProcessedStack = ItemStack.EMPTY;
    }

    public void retryOpenAuction() {
        this.attemptCount++;
        if (this.attemptCount <= maxAttempts) {
            setAuctionState(AuctionState.OPEN_AH);
        } else {
            notifyError("[Auction Relist] ÐÐµ ÑƒÐ´Ð°Ð»Ð¾ÑÑŒ Ð¾Ñ‚ÐºÑ€Ñ‹Ñ‚ÑŒ Ð°ÑƒÐºÑ†Ð¸Ð¾Ð½ Ð¿Ð¾ÑÐ»Ðµ 3 Ð¿Ð¾Ð¿Ñ‹Ñ‚Ð¾Ðº.");
            setState(false);
        }
    }

    public void notifyError(String str) {
        Expensive.INSTANCE.notificationRepository().post(NotificationType.ERROR, Text.of(str), 3L, TimeUnit.SECONDS);
    }

    @Override
    public void deactivate() {
        this.relistQueue.clear();
        this.currentState = AuctionState.IDLE;
        this.selling = false;
        this.actionStopwatch.reset();
        resetStorageState();
        this.currentItem = null;
        this.attemptCount = 0;
        this.cycleStopwatch.setElapsedTime((long) this.cycleTimeSetting.currentValue(), TimeUnit.SECONDS);
        super.deactivate();
    }
}
