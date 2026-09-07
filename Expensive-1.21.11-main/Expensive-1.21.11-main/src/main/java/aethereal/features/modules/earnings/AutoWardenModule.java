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


import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Aliases(aliases = {"Auto Warden", "Warden Farm", "Warden Bot", "Auto Ancient City", "Варден Бот"})
public class AutoWardenModule extends Module {
    public final NumberSetting homeAnarchy;
    public final BooleanSetting scoutCities;
    public final BooleanSetting autoSell;
    public final NumberSetting investPercent;
    public final BooleanSetting autoReporter;
    public final BooleanSetting tgNotify;
    public final Mc mc;

    public enum Phase {
        IDLE,
        SEARCHING,
        PATROL,
        MOVING,
        LOOTING,
        DEPOSIT,
        RESTOCK,
        SELL_ITEMS
    }

    public Phase currentPhase = Phase.IDLE;
    public BlockPos targetChestPos = null;

    public AutoWardenModule() {
        super(ModuleTab.EARNINGS, "Auto Warden");
        this.homeAnarchy = new NumberSetting(Translation.clearText("Домашняя анархия"), Translation.clearText("Номер домашней анархии")).currentValue(1.0f).range(1.0f, 100.0f).step(1.0f);
        this.scoutCities = new BooleanSetting(Translation.clearText("Разведка древних городов"), Translation.clearText("Автоматический поиск нелутанных древних городов"));
        this.autoSell = new BooleanSetting(Translation.clearText("Авто-продажа"), Translation.clearText("Автоматически выставлять ценный лут на продажу"));
        this.investPercent = new NumberSetting(Translation.clearText("Процент инвестиций"), Translation.clearText("Процент средств для реинвестиций")).currentValue(100.0f).range(0.0f, 100.0f).step(1.0f).unit(SettingUnit.PERCENTS);
        this.autoReporter = new BooleanSetting(Translation.clearText("Авто-репортер"), Translation.clearText("Автоматически отправлять жалобы на игроков при нападении"));
        this.tgNotify = new BooleanSetting(Translation.clearText("Telegram Уведомления"), Translation.clearText("Отправка уведомления о статусе в Telegram"));
        this.mc = Mc.INSTANCE;

        addSettings(this.homeAnarchy, this.scoutCities, this.autoSell, this.investPercent, this.autoReporter, this.tgNotify);

        register(PlayerTickEvent.class, event -> {
            if (!isState() || !this.mc.isWorldLoaded() || !event.isPre()) {
                return;
            }
            ClientPlayerEntity player= this.mc.getPlayer();
            if (player == null) {
                return;
            }

            tickAutoWarden(player);
        });
    }

    private void tickAutoWarden(ClientPlayerEntity player) {
        BlockPos playerPos= player.getBlockPos();

        if (this.targetChestPos == null || !this.mc.getWorld().getBlockState(this.targetChestPos).isOf(Blocks.CHEST)) {
            this.targetChestPos = findNearestChest(playerPos);
        }

        if (this.targetChestPos != null) {
            if (player.getEntityPos().distanceTo(this.targetChestPos.toCenterPos()) <= 4.5d) {
                this.mc.getInteractionManager().interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(this.targetChestPos.toCenterPos(), Direction.UP, this.targetChestPos, false));
                this.currentPhase = Phase.LOOTING;
            }
        }
    }

    private BlockPos findNearestChest(BlockPos playerPos) {
        for (int x = -16; x <= 16; x++) {
            for (int z = -16; z <= 16; z++) {
                for (int y = -8; y <= 8; y++) {
                    BlockPos pos= playerPos.add(x, y, z);
                    if (this.mc.getWorld().getBlockState(pos).isOf(Blocks.CHEST) || this.mc.getWorld().getBlockState(pos).isOf(Blocks.TRAPPED_CHEST)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void deactivate() {
        this.currentPhase = Phase.IDLE;
        this.targetChestPos = null;
        super.deactivate();
    }
}
