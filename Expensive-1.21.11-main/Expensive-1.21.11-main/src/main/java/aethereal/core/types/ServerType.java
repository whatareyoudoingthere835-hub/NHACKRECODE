package aethereal.core.types;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;

public enum ServerType implements DisplayNamed {
    FUNTIME(Translation.clearText("FunTime"), new DuelStrategy() {
        final Set<String> dueledPlayers = new HashSet();
        final Stopwatch duelDelayTimer = new Stopwatch();
        final Stopwatch screenClickTimer = new Stopwatch();

        @Override
        public void doDuelLogic(DuelContext class445Var) {
            Mc class815Var= Mc.INSTANCE;
            Screen currentScreen= class815Var.getCurrentScreen();
            if (!ServerUtil.isConnectedToServer("funtime")) {
                ChatUtil.addChatMessage("Ð’Ñ‹ Ð´Ð¾Ð»Ð¶Ð½Ñ‹ Ð±Ñ‹Ñ‚ÑŒ Ð¿Ð¾Ð´ÐºÐ»ÑŽÑ‡ÐµÐ½Ñ‹ Ðº " + String.valueOf(Formatting.RED) + "FunTime!");
                resetDuelState(class445Var);
                return;
            }
            if (ScoreboardHelper.INSTANCE.headerContains("Ñ…Ð°Ð±")) {
                ChatUtil.addChatMessage("Ð’Ñ‹ Ð´Ð¾Ð»Ð¶Ð½Ñ‹ Ð±Ñ‹Ñ‚ÑŒ Ð¿Ð¾Ð´ÐºÐ»ÑŽÑ‡ÐµÐ½Ñ‹ Ðº " + String.valueOf(Formatting.RED) + "Ð°Ð½Ð°Ñ€Ñ…Ð¸Ð¸!");
                resetDuelState(class445Var);
                return;
            }
            if (ScoreboardHelper.INSTANCE.headerContains("Ð³Ñ€Ð¸Ñ„ÐµÑ€ÑÐºÐ¸Ð¹")) {
                ChatUtil.addChatMessage("Ð”Ð°Ð½Ð½Ñ‹Ð¹ Ð¼Ð¾Ð´ÑƒÐ»ÑŒ Ñ€Ð°Ð±Ð¾Ñ‚Ð°ÐµÑ‚ Ñ‚Ð¾Ð»ÑŒÐºÐ¾ Ð½Ð° " + String.valueOf(Formatting.RED) + "Ð°Ð½Ð°Ñ€Ñ…Ð¸Ð¸!");
                resetDuelState(class445Var);
                return;
            }
            if (currentScreen instanceof GenericContainerScreen) {
                return;
            }
            if (this.duelDelayTimer.hasElapsed((long) class445Var.nextDuelDelay().currentValue(), TimeUnit.MILLISECONDS)) {
                ClientWorld world= class815Var.getWorld();
                PlayerEntity player= class815Var.getPlayer();
                AutoDuelArmorType class440VarArmorType= class445Var.armorType();
                AutoDuelOffhandItem class439VarOffhandItem= class445Var.offhandItem();
                for (PlayerEntity playerEntity : world.getPlayers()) {
                    if (playerEntity != player) {
                        String strStripTextFormat= StringHelper.stripTextFormat(playerEntity.getName().getString());
                        if (StringUtil.hasIllegalCharacter(strStripTextFormat)) {
                            return;
                        }
                        if (!this.dueledPlayers.contains(strStripTextFormat) && matchesArmor(playerEntity, class440VarArmorType) && matchesOffhand(playerEntity, class439VarOffhandItem)) {
                            ((ClientPlayerEntity) player).networkHandler.sendChatCommand("duel " + strStripTextFormat);
                            this.dueledPlayers.add(strStripTextFormat);
                            this.duelDelayTimer.reset();
                            return;
                        }
                    }
                }
            }
        }

        void resetDuelState(DuelContext class445Var) {
            class445Var.callback().deactivateModule();
            this.dueledPlayers.clear();
            this.screenClickTimer.reset();
            this.duelDelayTimer.reset();
        }

        boolean matchesArmor(PlayerEntity playerEntity, AutoDuelArmorType class440Var) {
            if (class440Var == AutoDuelArmorType.ANY) {
                return EquipmentUtil.armor(playerEntity).stream().noneMatch((v0) -> {
                    return v0.isEmpty();
                });
            }
            if (class440Var == AutoDuelArmorType.NONE) {
                return EquipmentUtil.armor(playerEntity).stream().allMatch((v0) -> {
                    return v0.isEmpty();
                });
            }
            List<Item> listItems= class440Var.items();
            return EquipmentUtil.armor(playerEntity).stream().allMatch(itemStack -> {
                return listItems.contains(itemStack.getItem());
            });
        }

        boolean matchesOffhand(PlayerEntity playerEntity, AutoDuelOffhandItem class439Var) throws MatchException {
            ItemStack offHandStack= playerEntity.getOffHandStack();
            String string= offHandStack.getName().getString();
            switch (OffhandItemSwitchMap.ordinalToCase[class439Var.ordinal()]) {
                case 1:
                    return offHandStack.getItem() == Items.TOTEM_OF_UNDYING;
                case 2:
                    return offHandStack.getItem() == Items.PLAYER_HEAD && string.toLowerCase().contains("ÑÑ„ÐµÑ€Ð°");
                case 3:
                    return true;
                default:
                    throw new MatchException((String) null, (Throwable) null);
            }
        }

        @Override
        public void onScreen(HandledScreenRenderEvent class015Var, DuelContext class445Var) {
            Mc class815Var= Mc.INSTANCE;
            ClientPlayerInteractionManager interactionManager= class815Var.getInteractionManager();
            ClientPlayerEntity player= class815Var.getPlayer();
            if ((class815Var.getCurrentScreen()) instanceof GenericContainerScreen currentScreen ) {
                GenericContainerScreenHandler screenHandler= currentScreen.getScreenHandler();
                if (StringHelper.stripTextFormat(currentScreen.getTitle().getString()).contains("Ð”ÑƒÑÐ»ÑŒ") && this.screenClickTimer.hasElapsed(100L, TimeUnit.MILLISECONDS)) {
                    interactionManager.clickSlot(screenHandler.syncId, 40, 0, SlotActionType.QUICK_MOVE, player);
                    this.screenClickTimer.reset();
                }
            }
        }

        @Override
        public void onChat(String str, DuelContext class445Var) {
            if (str.contains("Ð¿Ñ€Ð¸ÑÐ¾ÐµÐ´Ð¸Ð½Ð¸Ð»ÑÑ Ðº Ð´ÑƒÑÐ»Ð¸") || str.contains("Ð´ÑƒÑÐ»ÑŒ Ð½Ð°Ñ‡Ð½ÐµÑ‚ÑÑ")) {
                resetDuelState(class445Var);
            }
        }

        @Override
        public void deactivate() {
            this.dueledPlayers.clear();
            this.screenClickTimer.reset();
            this.duelDelayTimer.reset();
        }
    }),
    REALLYWORLD(Translation.clearText("ReallyWorld"), new DuelStrategy() {
        final Stopwatch nextDuelTimer = new Stopwatch();
        final Stopwatch screenActionTimer = new Stopwatch();
        int playerIndex = 0;

        @Override
        public void doDuelLogic(DuelContext class445Var) {
            Mc class815Var= Mc.INSTANCE;
            ClientPlayerEntity player= class815Var.getPlayer();
            if (!ServerUtil.isConnectedToServer("reallyworld") && !ServerUtil.isConnectedToServer("rwcopy")) {
                ChatUtil.addChatMessage("Ð’Ñ‹ Ð´Ð¾Ð»Ð¶Ð½Ñ‹ Ð±Ñ‹Ñ‚ÑŒ Ð¿Ð¾Ð´ÐºÐ»ÑŽÑ‡ÐµÐ½Ñ‹ Ðº " + String.valueOf(Formatting.RED) + "ReallyWorld!");
                resetDuelState(class445Var);
                return;
            }
            if (ScoreboardHelper.INSTANCE.headerContains("Lobby")) {
                ChatUtil.addChatMessage("Ð’Ñ‹ Ð´Ð¾Ð»Ð¶Ð½Ñ‹ Ð±Ñ‹Ñ‚ÑŒ Ð¿Ð¾Ð´ÐºÐ»ÑŽÑ‡ÐµÐ½Ñ‹ Ðº " + String.valueOf(Formatting.RED) + "Ð°Ð½Ð°Ñ€Ñ…Ð¸Ð¸/Ð³Ñ€Ð¸Ñ„Ñƒ!");
                resetDuelState(class445Var);
                return;
            }
            Screen currentScreen= class815Var.getCurrentScreen();
            ArrayList arrayList= new ArrayList(player.networkHandler.getPlayerList());
            if (arrayList.isEmpty()) {
                this.playerIndex = 0;
                return;
            }
            if (this.playerIndex >= arrayList.size()) {
                this.playerIndex = 0;
                return;
            }
            String strStripTextFormat= StringHelper.stripTextFormat(((PlayerListEntry) arrayList.get(this.playerIndex)).getProfile().name());
            if (StringUtil.hasIllegalCharacter(strStripTextFormat)) {
                this.playerIndex++;
                return;
            }
            if (!(currentScreen instanceof GenericContainerScreen) && this.nextDuelTimer.hasElapsed((long) class445Var.nextDuelDelay().currentValue(), TimeUnit.MILLISECONDS)) {
                if (strStripTextFormat.equals(Mc.INSTANCE.getSession().getUsername())) {
                    this.playerIndex++;
                    return;
                }
                player.networkHandler.sendChatCommand("duel " + strStripTextFormat);
                this.playerIndex++;
                this.nextDuelTimer.reset();
            }
        }

        @Override
        public void onScreen(HandledScreenRenderEvent class015Var, DuelContext class445Var) {
            Mc class815Var= Mc.INSTANCE;
            ClientPlayerInteractionManager interactionManager= class815Var.getInteractionManager();
            ClientPlayerEntity player= class815Var.getPlayer();
            if ((class815Var.getCurrentScreen()) instanceof GenericContainerScreen currentScreen ) {
                GenericContainerScreenHandler screenHandler= currentScreen.getScreenHandler();
                String strStripTextFormat= StringHelper.stripTextFormat(currentScreen.getTitle().getString());
                if (!strStripTextFormat.contains("Ð’Ñ‹Ð±Ð¾Ñ€ Ð½Ð°Ð±Ð¾Ñ€Ð° (1/1)")) {
                    if (strStripTextFormat.contains("ÐÐ°ÑÑ‚Ñ€Ð¾Ð¹ÐºÐ° Ð¿Ð¾ÐµÐ´Ð¸Ð½ÐºÐ°") && this.screenActionTimer.hasElapsed(200L, TimeUnit.MILLISECONDS)) {
                        interactionManager.clickSlot(screenHandler.syncId, 0, 0, SlotActionType.QUICK_MOVE, player);
                        this.screenActionTimer.reset();
                        return;
                    }
                    return;
                }
                MultiSelectSetting<DuelKitType> class671VarKits= class445Var.kits();
                for (int i = 0; i < screenHandler.getInventory().size(); i++) {
                    ArrayList arrayList= new ArrayList();
                    int i2= 0;
                    for (DuelKitType class442Var : (DuelKitType[]) class671VarKits.options()) {
                        if (class671VarKits.isSelected(class442Var)) {
                            arrayList.add(Integer.valueOf(i2));
                        }
                        i2++;
                    }
                    Collections.shuffle(arrayList);
                    int iIntValue= ((Integer) arrayList.getFirst()).intValue();
                    if (this.screenActionTimer.hasElapsed(200L, TimeUnit.MILLISECONDS)) {
                        interactionManager.clickSlot(screenHandler.syncId, iIntValue, 0, SlotActionType.PICKUP, player);
                        this.screenActionTimer.reset();
                    }
                }
            }
        }

        @Override
        public void onChat(String str, DuelContext class445Var) {
            if ((str.contains("Ð½Ð°Ñ‡Ð°Ð»Ð¾") && str.contains("Ñ‡ÐµÑ€ÐµÐ·") && str.contains("ÑÐµÐºÑƒÐ½Ð´!")) || str.equals("Ð´ÑƒÑÐ»Ð¸ Â» Ð²Ð¾ Ð²Ñ€ÐµÐ¼Ñ Ð¿Ð¾ÐµÐ´Ð¸Ð½ÐºÐ° Ð·Ð°Ð¿Ñ€ÐµÑ‰ÐµÐ½Ð¾ Ð¸ÑÐ¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÑŒ ÐºÐ¾Ð¼Ð°Ð½Ð´Ñ‹")) {
                resetDuelState(class445Var);
            } else if (str.contains("Ð´Ð»Ñ ÑƒÑ‡Ð°ÑÑ‚Ð¸Ñ Ð² ÑÑ‚Ð¾Ð¼ Ð¿Ð¾ÐµÐ´Ð¸Ð½ÐºÐµ Ñƒ Ð²Ð°Ñ Ð½ÐµÐ´Ð¾ÑÑ‚Ð°Ñ‚Ð¾Ñ‡Ð½Ð¾ Ð´ÐµÐ½ÐµÐ³")) {
                resetDuelState(class445Var);
            } else if (str.contains("ÐºÐ¾Ð¼Ð°Ð½Ð´Ñ‹ Ð½ÐµÐ´Ð¾ÑÑ‚ÑƒÐ¿Ð½Ñ‹ Ð² pvp Ñ€ÐµÐ¶Ð¸Ð¼Ðµ")) {
                resetDuelState(class445Var);
            }
        }

        void resetDuelState(DuelContext class445Var) {
            this.playerIndex = 0;
            class445Var.callback().deactivateModule();
        }

        @Override
        public void deactivate() {
            this.playerIndex = 0;
            this.screenActionTimer.reset();
            this.nextDuelTimer.reset();
        }
    });

    public final Translation displayName;
    public final DuelStrategy strategy;

    @Override
    public Translation getDisplayName() {
        return this.displayName;
    }

    public Translation displayName() {
        return this.displayName;
    }

    public DuelStrategy strategy() {
        return this.strategy;
    }

    ServerType(Translation class254Var, DuelStrategy class444Var) {
        this.displayName = class254Var;
        this.strategy = class444Var;
    }
}
