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
import net.minecraft.util.Formatting;

import java.util.Locale;

@Aliases(aliases = {"Auto Mine", "AutoMine", "Авто шахта", "Авто майн"})
public class AutoMineModule extends Module {

    public enum Phase {
        IDLE,
        WARP_MINE,
        MINE,
        SELL_ORES,
        BUY_BOTTLES,
        REPAIR,
        GO_HOME,
        DEPOSIT
    }

    public final Mc mc;

    public final BooleanSetting keepOres;
    public final BooleanSetting oreCoal;
    public final BooleanSetting oreLapis;
    public final BooleanSetting oreRedstone;
    public final BooleanSetting oreIron;
    public final BooleanSetting oreCopper;
    public final BooleanSetting oreGold;
    public final BooleanSetting oreDiamond;
    public final BooleanSetting oreEmerald;
    public final BooleanSetting oreNetherite;
    public final ExpandableSetting keptOres;

    public final BooleanSetting buyerAutoSell;
    public final TextFieldSetting homeName;
    public final BooleanSetting repairBottles;
    public final NumberSetting repairThreshold;
    public final NumberSetting oreSwapPercent;
    public final NumberSetting oreSwapPercentRare;
    public final NumberSetting mineWaitMax;

    private Phase currentPhase = Phase.IDLE;
    private long lastActionTime = 0L;
    private int stuckCounter = 0;

    public AutoMineModule() {
        super(ModuleTab.EARNINGS, "Auto Mine");
        this.mc = Mc.INSTANCE;

        this.keepOres = new BooleanSetting(
            Translation.clearText("Оставлять руды"),
            Translation.clearText("Сохранять выбранные руды в инвентаре")
        );
        this.oreCoal      = new BooleanSetting(Translation.clearText("Уголь"));
        this.oreLapis     = new BooleanSetting(Translation.clearText("Лазурит"));
        this.oreRedstone  = new BooleanSetting(Translation.clearText("Редстоун"));
        this.oreIron      = new BooleanSetting(Translation.clearText("Железо")).setValue(true);
        this.oreCopper    = new BooleanSetting(Translation.clearText("Медь"));
        this.oreGold      = new BooleanSetting(Translation.clearText("Золото")).setValue(true);
        this.oreDiamond   = new BooleanSetting(Translation.clearText("Алмазы")).setValue(true);
        this.oreEmerald   = new BooleanSetting(Translation.clearText("Изумруды")).setValue(true);
        this.oreNetherite = new BooleanSetting(Translation.clearText("Незерит")).setValue(true);

        this.keptOres = new ExpandableSetting(
            Translation.clearText("Руды для сохранения"),
            Translation.clearText("Выберите какие руды оставлять")
        ).settings(oreCoal, oreLapis, oreRedstone, oreIron, oreCopper, oreGold, oreDiamond, oreEmerald, oreNetherite)
         .visible(keepOres::isValue);

        this.buyerAutoSell = new BooleanSetting(
            Translation.clearText("Авто-продажа"),
            Translation.clearText("Автоматически продавать добытые ресурсы через /shop")
        );
        this.homeName = new TextFieldSetting(
            Translation.clearText("Название дома"),
            Translation.clearText("Команда /home <name> для телепортации на шахту")
        ).setText("mine");

        this.repairBottles = new BooleanSetting(
            Translation.clearText("Покупать бутылки XP"),
            Translation.clearText("Покупать бутылки опыта для починки инструментов")
        );
        this.repairThreshold = new NumberSetting(
            Translation.clearText("Порог ремонта (%)"),
            Translation.clearText("Чинить инструмент при прочности ниже порога")
        ).range(5f, 50f);
        this.repairThreshold.setCurrentValue(20f);

        this.oreSwapPercent = new NumberSetting(
            Translation.clearText("% слотов для руд"),
            Translation.clearText("Процент слотов инвентаря для обычных руд")
        ).range(10f, 90f);
        this.oreSwapPercent.setCurrentValue(50f);

        this.oreSwapPercentRare = new NumberSetting(
            Translation.clearText("% слотов для редких руд"),
            Translation.clearText("Процент слотов для редких руд (алмазы, изумруды)")
        ).range(10f, 90f);
        this.oreSwapPercentRare.setCurrentValue(30f);

        this.mineWaitMax = new NumberSetting(
            Translation.clearText("Макс. ожидание (сек)"),
            Translation.clearText("Максимальное время между действиями в секундах")
        ).range(1f, 60f);
        this.mineWaitMax.setCurrentValue(10f);

        addSettings(
            keepOres, keptOres,
            buyerAutoSell,
            homeName,
            repairBottles, repairThreshold,
            oreSwapPercent, oreSwapPercentRare,
            mineWaitMax
        );

        register(PlayerTickEvent.class, event -> {
            if (!isState() || !this.mc.isWorldLoaded() || !event.isPre()) return;
            ClientPlayerEntity player= this.mc.getPlayer();
            if (player == null) return;
            tick(player);
        });

        register(ChatReceiveEvent.class, event -> {
            if (!isState()) return;
            String msg= Formatting.strip(event.message());
            if (msg == null) return;
            handleChat(msg.toLowerCase(Locale.ROOT));
        });

        register(WorldLoadEvent.class, event -> resetState());
    }

    private void tick(ClientPlayerEntity player) {
        long now= System.currentTimeMillis();
        long waitMs= (long)(mineWaitMax.currentValue * 1000);

        switch (currentPhase) {
            case IDLE -> {
                if (now - lastActionTime > 2000) {
                    setPhase(Phase.WARP_MINE);
                }
            }
            case WARP_MINE -> {
                if (now - lastActionTime > 1500) {
                    String home= homeName.text;
                    if (home == null || home.isBlank()) home = "mine";
                    player.networkHandler.sendChatMessage("/home " + home);
                    lastActionTime = now + waitMs;
                }
            }
            case MINE -> {
                checkInventoryAndTools(player, now);
            }
            case SELL_ORES -> {
                if (now - lastActionTime > 1000) {
                    player.networkHandler.sendChatMessage("/shop");
                    lastActionTime = now + waitMs;
                }
            }
            case BUY_BOTTLES -> {
                if (now - lastActionTime > 1000) {
                    player.networkHandler.sendChatMessage("/shop repair");
                    lastActionTime = now + waitMs;
                }
            }
            case REPAIR -> {
                if (now - lastActionTime > 3000) {
                    setPhase(Phase.WARP_MINE);
                }
            }
            case GO_HOME -> {
                if (now - lastActionTime > 1000) {
                    player.networkHandler.sendChatMessage("/home");
                    lastActionTime = now + waitMs;
                }
            }
            case DEPOSIT -> {
                if (now - lastActionTime > 3000) {
                    setPhase(Phase.WARP_MINE);
                }
            }
        }
    }

    private void checkInventoryAndTools(ClientPlayerEntity player, long now) {
        // Check tool durability
        for (int i = 0; i < player.getInventory().size(); i++) {
            var stack= player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getMaxDamage() <= 0) continue;
            float durabilityPct= 100f - (stack.getDamage() * 100f / (float) stack.getMaxDamage());
            if (durabilityPct < repairThreshold.currentValue) {
                setPhase(repairBottles.isValue() ? Phase.BUY_BOTTLES : Phase.REPAIR);
                return;
            }
        }
        // Check inventory full
        if (buyerAutoSell.isValue()) {
            int empty= 0;
            for (int i = 0; i < 36; i++) {
                if (player.getInventory().getStack(i).isEmpty()) empty++;
            }
            if (empty <= 2) {
                setPhase(Phase.SELL_ORES);
            }
        }
    }

    private void handleChat(String msg) {
        if (msg.contains("переместил") || msg.contains("телепорт") || msg.contains("teleport")) {
            if (currentPhase == Phase.WARP_MINE) {
                setPhase(Phase.MINE);
            } else if (currentPhase == Phase.GO_HOME) {
                setPhase(Phase.DEPOSIT);
            }
        }
        if (msg.contains("продано") || msg.contains("sold") || msg.contains("магазин")) {
            if (currentPhase == Phase.SELL_ORES) {
                setPhase(Phase.WARP_MINE);
            }
        }
        if (msg.contains("заморозить") || msg.contains("вы не можете") || msg.contains("нельзя")) {
            stuckCounter++;
            if (stuckCounter > 3) {
                setPhase(Phase.IDLE);
                stuckCounter = 0;
            }
        }
    }

    private void setPhase(Phase phase) {
        this.currentPhase = phase;
        this.lastActionTime = System.currentTimeMillis();
        this.stuckCounter = 0;
    }

    private void resetState() {
        currentPhase = Phase.IDLE;
        lastActionTime = 0L;
        stuckCounter = 0;
    }

    @Override
    public void deactivate() {
        resetState();
        super.deactivate();
    }
}
