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
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Aliases(aliases = {"Auto Trade", "AutoTrade", "Авто торговля", "Авто трейд", "Villager Trade Bot"})
public class AutoTradeModule extends Module {

    // === Phases (1:1 from aJ.java In004) ===
    public enum Phase {
        IDLE,
        SCAN_SELECT,    // Ищем жителей / сундуки
        SCAN_MOVE,      // Двигаемся к цели
        SCAN_OPEN,      // Открываем инвентарь/торговлю
        BUY_EMERALDS,   // Покупаем изумруды через /shop
        TRADE_SELECT,   // Выбираем жителя для торговли
        TRADE_MOVE,     // Двигаемся к жителю
        TRADE_OPEN,     // Открываем торговлю с жителем
        DEPOSIT_FIND,   // Ищем золото-сундук
        DEPOSIT_MOVE,   // Двигаемся к сундуку
        DEPOSIT_OPEN,   // Открываем сундук
        DEPOSIT_ITEMS,  // Перекладываем золото в сундук
        CRAFT_FIND,     // Ищем верстак
        CRAFT_MOVE,     // Двигаемся к верстаку
        CRAFT_OPEN,     // Открываем верстак
        CRAFT_ITEMS,    // Крафтим золотые блоки
        WITHDRAW_FIND,  // Ищем сундук для забора
        WITHDRAW_MOVE,  // Двигаемся к сундуку
        WITHDRAW_OPEN,  // Открываем сундук
        WITHDRAW_ITEMS, // Забираем золото
        WAIT            // Ждём (ресток жителей и т.д.)
    }

    // === Settings (1:1 из SettingKey AUTOTRADE_*) ===
    public final BooleanSetting buyEmeralds;
    public final NumberSetting emeraldReserve;
    public final NumberSetting scanRadius;
    public final BooleanSetting depositGold;
    public final NumberSetting chestScanRadius;
    public final TextFieldSetting chestKeyword;
    public final BooleanSetting autoSellBlocks;
    public final BooleanSetting craftBlocks;
    public final TextFieldSetting auctionQuery;
    public final BooleanSetting restockCheck;

    // === State ===
    public final Mc mc;
    public Phase currentPhase = Phase.IDLE;

    // Timers (Cls240 → Stopwatch)
    public final Stopwatch actionTimer   = new Stopwatch();
    public final Stopwatch cycleTimer    = new Stopwatch();
    public final Stopwatch notifyTimer   = new Stopwatch();
    public final Stopwatch restockTimer  = new Stopwatch();
    public final Stopwatch buyTimer      = new Stopwatch();

    // Trade-target villager
    public VillagerEntity targetVillager = null;

    // Gold chest position
    public BlockPos goldChestPos   = null;
    public BlockPos craftTablePos  = null;

    // Blacklist of broken chest positions
    public java.util.Set<BlockPos> blacklistedChests = new java.util.HashSet<>();

    // Counters
    public int retryCount = 0;
    public int tradesDone = 0;
    public long moneyNeeded = 0L;
    public long moneyBalance = -1L;

    // Restock position tracking (like aJ Cls031 / Cls208 for back-and-forth)
    public BlockPos restockPosA = null;
    public BlockPos restockPosB = null;
    public boolean restockSide  = false;
    public long restockSwitchTime = 0L;

    public AutoTradeModule() {
        super(ModuleTab.EARNINGS, "Auto Trade");
        this.mc = Mc.INSTANCE;

        // === Settings init ===
        this.buyEmeralds = new BooleanSetting(
            Translation.clearText("Покупать изумруды"),
            Translation.clearText("Автоматически покупать изумруды через /shop при нехватке")
        ).setValue(true);

        this.emeraldReserve = new NumberSetting(
            Translation.clearText("Резерв изумрудов"),
            Translation.clearText("Минимум изумрудов для начала торговли")
        ).range(1f, 9999f);
        this.emeraldReserve.setCurrentValue(64f);

        this.scanRadius = new NumberSetting(
            Translation.clearText("Радиус поиска жителей"),
            Translation.clearText("Радиус в блоках для поиска торговцев-жрецов")
        ).range(5f, 64f);
        this.scanRadius.setCurrentValue(16f);

        this.depositGold = new BooleanSetting(
            Translation.clearText("Складывать золото"),
            Translation.clearText("Складывать золотые слитки/блоки в сундук с ключевым словом")
        ).setValue(true);

        this.chestScanRadius = new NumberSetting(
            Translation.clearText("Радиус поиска сундука"),
            Translation.clearText("Радиус поиска сундука для складирования золота (блоки)")
        ).range(5f, 64f);
        this.chestScanRadius.setCurrentValue(10f);

        this.chestKeyword = new TextFieldSetting(
            Translation.clearText("Слово на табличке"),
            Translation.clearText("Надпись на табличке у сундука (для складирования золота)")
        ).setText("золото");

        this.autoSellBlocks = new BooleanSetting(
            Translation.clearText("Продавать блоки"),
            Translation.clearText("Выставлять золотые блоки на аукцион через /ah")
        );

        this.craftBlocks = new BooleanSetting(
            Translation.clearText("Крафтить блоки"),
            Translation.clearText("Крафтить золотые блоки из слитков на ближайшем верстаке")
        ).setValue(true);

        this.auctionQuery = new TextFieldSetting(
            Translation.clearText("Запрос для аукциона"),
            Translation.clearText("Команда поиска для выставления золотых блоков на ауке")
        ).setText("ah search золотой блок");

        this.restockCheck = new BooleanSetting(
            Translation.clearText("Ждать ресток жителей"),
            Translation.clearText("Двигаться туда-сюда ожидая восстановление сделок у жителей")
        ).setValue(true);

        addSettings(
            buyEmeralds, emeraldReserve,
            scanRadius,
            depositGold, chestScanRadius, chestKeyword,
            craftBlocks, autoSellBlocks, auctionQuery,
            restockCheck
        );

        // === Events ===
        register(PlayerTickEvent.class, event -> {
            if (!isState() || !mc.isWorldLoaded() || !event.isPre()) return;
            ClientPlayerEntity player= mc.getPlayer();
            if (player == null) return;
            tick(player);
        });

        register(PacketReceiveEvent.class, event -> {
            if (!isState() || !mc.isWorldLoaded()) return;
            if (event.getPacket() instanceof GameMessageS2CPacket packet) {
                handleChatPacket(packet.content().getString());
            }
        });

        register(WorldLoadEvent.class, event -> resetState());
    }

    // ======================================================
    // MAIN TICK
    // ======================================================
    private void tick(ClientPlayerEntity player) {
        long now= System.currentTimeMillis();

        switch (currentPhase) {

            case IDLE -> {
                if (actionTimer.hasElapsed(1000)) {
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            // ── Выбираем жителя ──
            case TRADE_SELECT -> {
                // Если нужно скрафтить золотые блоки
                if (craftBlocks.isValue() && shouldCraft(player)) {
                    setPhase(Phase.CRAFT_FIND);
                    return;
                }
                // Если нужно сдать золото
                if (depositGold.isValue() && shouldDeposit(player)) {
                    setPhase(Phase.DEPOSIT_FIND);
                    return;
                }
                // Если нет изумрудов — покупаем
                int emeralds= countItem(player, Items.EMERALD);
                if (emeralds < (int) emeraldReserve.currentValue && buyEmeralds.isValue()) {
                    setPhase(Phase.BUY_EMERALDS);
                    return;
                }
                // Ищем жителя
                VillagerEntity villager= findBestVillager(player);
                if (villager != null) {
                    targetVillager = villager;
                    setPhase(Phase.TRADE_MOVE);
                } else if (restockCheck.isValue()) {
                    // Начинаем ожидать ресток — ходить туда-сюда
                    setupRestockWalk(player);
                    setPhase(Phase.WAIT);
                } else {
                    // Ждём просто
                    if (actionTimer.hasElapsed(5000)) {
                        actionTimer.reset();
                    }
                }
            }

            // ── Движение к жителю ──
            case TRADE_MOVE -> {
                if (targetVillager == null || !targetVillager.isAlive()) {
                    setPhase(Phase.TRADE_SELECT);
                    return;
                }
                double dist= player.distanceTo(targetVillager);
                if (dist <= 3.0) {
                    setPhase(Phase.TRADE_OPEN);
                    return;
                }
                // Стак-детект: если долго не можем дойти
                if (actionTimer.hasElapsed(15000)) {
                    notify("Не могу добраться до жителя — пропускаю", NotificationType.ERROR);
                    targetVillager = null;
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            // ── Открываем торговлю ──
            case TRADE_OPEN -> {
                if (targetVillager == null || !targetVillager.isAlive()) {
                    setPhase(Phase.TRADE_SELECT);
                    return;
                }
                if (actionTimer.hasElapsed(600)) {
                    // Открываем окно торговли
                    mc.getInteractionManager().interactEntity(player, targetVillager, Hand.MAIN_HAND);
                    actionTimer.reset();
                    // Переходим к торговле
                    setPhase(Phase.SCAN_OPEN);
                }
            }

            // ── Торгуем (окно открыто) ──
            case SCAN_OPEN -> {
                if (mc.getCurrentScreen() instanceof net.minecraft.client.gui.screen.ingame.MerchantScreen merchantScreen) {
                    MerchantScreenHandler handler= (MerchantScreenHandler) merchantScreen.getScreenHandler();
                    if (!actionTimer.hasElapsed(400)) return;

                    boolean traded= tryTrade(handler, player);
                    if (!traded) {
                        // Нет доступных сделок — ресток
                        closeCurrent(player);
                        if (restockCheck.isValue()) {
                            setupRestockWalk(player);
                            setPhase(Phase.WAIT);
                        } else {
                            targetVillager = null;
                            setPhase(Phase.TRADE_SELECT);
                        }
                    } else {
                        tradesDone++;
                        actionTimer.reset();
                    }
                } else if (actionTimer.hasElapsed(3000)) {
                    // Экран не открылся — timeout
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            // ── Покупаем изумруды через /shop ──
            case BUY_EMERALDS -> {
                if (actionTimer.hasElapsed(1200)) {
                    player.networkHandler.sendChatMessage("/shop");
                    actionTimer.reset();
                    // Ждём подтверждения в чате
                    buyTimer.reset();
                    setPhase(Phase.WAIT);
                }
            }

            // ── Ищем сундук с золотом ──
            case DEPOSIT_FIND -> {
                goldChestPos = findChestWithKeyword(player);
                if (goldChestPos != null) {
                    blacklistedChests.remove(goldChestPos);
                    setPhase(Phase.DEPOSIT_MOVE);
                } else {
                    if (notifyTimer.hasElapsed(5000)) {
                        notify("Не нашёл золото-сундук рядом", NotificationType.ERROR);
                        notifyTimer.reset();
                    }
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            // ── Движение к сундуку ──
            case DEPOSIT_MOVE -> {
                if (goldChestPos == null) { setPhase(Phase.DEPOSIT_FIND); return; }
                double dist= player.getEntityPos().distanceTo(goldChestPos.toCenterPos());
                if (dist <= 4.0) {
                    setPhase(Phase.DEPOSIT_OPEN);
                    return;
                }
                if (actionTimer.hasElapsed(8000)) {
                    blacklistedChests.add(goldChestPos);
                    goldChestPos = null;
                    setPhase(Phase.DEPOSIT_FIND);
                }
            }

            // ── Открываем сундук ──
            case DEPOSIT_OPEN -> {
                if (goldChestPos == null) { setPhase(Phase.DEPOSIT_FIND); return; }
                if (actionTimer.hasElapsed(400)) {
                    // Открываем сундук взаимодействием с блоком
                    mc.getInteractionManager().interactBlock(
                        player, Hand.MAIN_HAND,
                        new net.minecraft.util.hit.BlockHitResult(
                            goldChestPos.toCenterPos(),
                            net.minecraft.util.math.Direction.UP,
                            goldChestPos, false
                        )
                    );
                    actionTimer.reset();
                    setPhase(Phase.DEPOSIT_ITEMS);
                }
            }

            // ── Складываем золото ──
            case DEPOSIT_ITEMS -> {
                if (mc.getCurrentScreen() instanceof GenericContainerScreen containerScreen) {
                    GenericContainerScreenHandler handler= (GenericContainerScreenHandler) containerScreen.getScreenHandler();
                    if (!actionTimer.hasElapsed(300)) return;

                    boolean deposited= depositGoldItems(player, handler);
                    if (!deposited) {
                        closeCurrent(player);
                        goldChestPos = null;
                        setPhase(Phase.TRADE_SELECT);
                    }
                } else if (actionTimer.hasElapsed(4000)) {
                    goldChestPos = null;
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            // ── Ищем верстак ──
            case CRAFT_FIND -> {
                craftTablePos = findCraftingTable(player);
                if (craftTablePos != null) {
                    setPhase(Phase.CRAFT_MOVE);
                } else {
                    if (notifyTimer.hasElapsed(5000)) {
                        notify("Верстак не найден рядом", NotificationType.ERROR);
                        notifyTimer.reset();
                    }
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            // ── Движение к верстаку ──
            case CRAFT_MOVE -> {
                if (craftTablePos == null) { setPhase(Phase.CRAFT_FIND); return; }
                double dist= player.getEntityPos().distanceTo(craftTablePos.toCenterPos());
                if (dist <= 3.5) {
                    setPhase(Phase.CRAFT_OPEN);
                    return;
                }
                if (actionTimer.hasElapsed(10000)) {
                    craftTablePos = null;
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            // ── Открываем верстак ──
            case CRAFT_OPEN -> {
                if (craftTablePos == null) { setPhase(Phase.CRAFT_FIND); return; }
                if (actionTimer.hasElapsed(400)) {
                    mc.getInteractionManager().interactBlock(
                        player, Hand.MAIN_HAND,
                        new net.minecraft.util.hit.BlockHitResult(
                            craftTablePos.toCenterPos(),
                            net.minecraft.util.math.Direction.UP,
                            craftTablePos, false
                        )
                    );
                    actionTimer.reset();
                    setPhase(Phase.CRAFT_ITEMS);
                }
            }

            // ── Крафтим золотые блоки ──
            case CRAFT_ITEMS -> {
                if (mc.getCurrentScreen() instanceof net.minecraft.client.gui.screen.ingame.CraftingScreen craftingScreen) {
                    if (!actionTimer.hasElapsed(400)) return;
                    // SHIFT+ЛКМ по слоту результата (0) для крафта максимума
                    int ingots= countItem(player, Items.GOLD_INGOT);
                    if (ingots >= 9) {
                        PlayerInventoryUtils.INSTANCE.windowClick(SlotActionType.QUICK_MOVE, 0, 0, true);
                        actionTimer.reset();
                    } else {
                        closeCurrent(player);
                        craftTablePos = null;
                        setPhase(Phase.TRADE_SELECT);
                    }
                } else if (actionTimer.hasElapsed(4000)) {
                    craftTablePos = null;
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            // ── Ожидание (ресток жителей / покупка изумрудов) ──
            case WAIT -> {
                tickRestockWalk(player, now);
                if (actionTimer.hasElapsed(Math.max(30000L, cycleTimer.getElapsedTime(TimeUnit.MILLISECONDS)))) {
                    retryCount++;
                    if (retryCount > 5) {
                        retryCount = 0;
                        notify("Слишком долго ждём — перезапускаем цикл", NotificationType.INFO);
                    }
                    setPhase(Phase.TRADE_SELECT);
                }
            }

            default -> setPhase(Phase.IDLE);
        }
    }

    // ======================================================
    // ТОРГОВЛЯ
    // ======================================================

    /** Пытаемся совершить сделку в открытом окне торговца.
     *  Возвращает true если сделка совершена. */
    private boolean tryTrade(MerchantScreenHandler handler, ClientPlayerEntity player) {
        // Ищем сделку: изумруды + ещё что-то → золото
        // Handler exposes getRecipes() in this Fabric version
        for (int i = 0; i < handler.getRecipes().size(); i++) {
            var offer= handler.getRecipes().get(i);
            if (offer.isDisabled()) continue;
            var result= offer.getSellItem();
            // Ищем сделки дающие золото (слитки или блоки)
            if (!result.isOf(Items.GOLD_INGOT) && !result.isOf(Items.GOLD_BLOCK)) continue;
            // Выбираем сделку
            PlayerInventoryUtils.INSTANCE.windowClick(SlotActionType.PICKUP, i, 0, false);
            return true;
        }
        return false;
    }

    // ======================================================
    // СКЛАДИРОВАНИЕ
    // ======================================================

    /** Перекладываем золото из инвентаря в открытый сундук. */
    private boolean depositGoldItems(ClientPlayerEntity player, GenericContainerScreenHandler handler) {
        boolean found= false;
        for (int i = 0; i < 36; i++) {
            var stack= player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.isOf(Items.GOLD_INGOT) || stack.isOf(Items.GOLD_BLOCK)) {
                // Слот инвентаря в экране сундука начинается после его слотов
                int screenSlot= handler.getInventory().size() + i;
                PlayerInventoryUtils.INSTANCE.windowClick(SlotActionType.QUICK_MOVE, screenSlot, 0, true);
                found = true;
                return true; // По одному за тик
            }
        }
        return found;
    }

    // ======================================================
    // РЕШЕНИЕ: что делать дальше (из Cls121(player) в aJ.java)
    // ======================================================

    /** Нужно ли крафтить? >= 9 золотых слитков и настройка включена. */
    private boolean shouldCraft(ClientPlayerEntity player) {
        return craftBlocks.isValue() && countItem(player, Items.GOLD_INGOT) >= 9;
    }

    /** Нужно ли сдать золото? Есть золото И (инвентарь почти полон ИЛИ много золота). */
    private boolean shouldDeposit(ClientPlayerEntity player) {
        int gold= countItem(player, Items.GOLD_INGOT) + countItem(player, Items.GOLD_BLOCK) * 9;
        if (gold <= 0) return false;
        int empty= getEmptySlots(player);
        return empty <= 2 || !hasEnoughSpace(player);
    }

    private boolean hasEnoughSpace(ClientPlayerEntity player) {
        return getEmptySlots(player) > 1;
    }

    private int getEmptySlots(ClientPlayerEntity player) {
        int empty= 0;
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getStack(i).isEmpty()) empty++;
        }
        return empty;
    }

    // ======================================================
    // РЕСТОК WALK (aJ.java Cls178: ходить туда-сюда)
    // ======================================================

    private void setupRestockWalk(ClientPlayerEntity player) {
        BlockPos pos= player.getBlockPos();
        restockPosA = pos;
        restockPosB = pos.offset(net.minecraft.util.math.Direction.NORTH, 2);
        restockSide = false;
        restockSwitchTime = System.currentTimeMillis() + 650;
        restockTimer.reset();
    }

    /** Ходим туда-сюда ожидая ресток жителя (логика из Cls178 в aJ.java). */
    private void tickRestockWalk(ClientPlayerEntity player, long now) {
        if (restockPosA == null || restockPosB == null) return;
        if (now < restockSwitchTime) return;
        BlockPos target= restockSide ? restockPosB : restockPosA;
        double dist= player.getEntityPos().distanceTo(target.toCenterPos());
        if (dist <= 1.2) {
            // Достигли точки — меняем направление
            restockSide = !restockSide;
            restockSwitchTime = now + 650;
        }
    }

    // ======================================================
    // ПОИСК ОБЪЕКТОВ В МИРЕ
    // ======================================================

    /** Найти ближайшего жителя (жреца) с доступными сделками на золото. */
    private VillagerEntity findBestVillager(ClientPlayerEntity player) {
        double radius= scanRadius.currentValue;
        Box box= player.getBoundingBox().expand(radius);
        List<VillagerEntity> list= mc.getWorld().getEntitiesByClass(
            VillagerEntity.class, box,
            v -> v.isAlive() && !v.isBaby() && !v.getOffers().isEmpty()
        );
        return list.stream()
            .filter(v -> v.getOffers().stream().anyMatch(o ->
                !o.isDisabled() && (o.getSellItem().isOf(Items.GOLD_INGOT) || o.getSellItem().isOf(Items.GOLD_BLOCK))
            ))
            .min(Comparator.comparingDouble(player::distanceTo))
            .orElse(null);
    }

    /** Найти сундук с табличкой содержащей ключевое слово рядом. */
    private BlockPos findChestWithKeyword(ClientPlayerEntity player) {
        String keyword= chestKeyword.text != null ? normalize(chestKeyword.text) : "золото";
        double radius= chestScanRadius.currentValue;
        BlockPos center= player.getBlockPos();
        int r= (int) radius;
        for (int x = -r; x <= r; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos= center.add(x, y, z);
                    if (blacklistedChests.contains(pos)) continue;
                    var state= mc.getWorld().getBlockState(pos);
                    if (!state.isOf(Blocks.CHEST) && !state.isOf(Blocks.TRAPPED_CHEST)) continue;
                    // Проверяем таблички вокруг
                    for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
                        BlockPos signPos= pos.offset(dir);
                        var signState= mc.getWorld().getBlockState(signPos);
                        if (signState.isOf(Blocks.OAK_SIGN) || signState.isOf(Blocks.OAK_WALL_SIGN)
                            || signState.isOf(Blocks.BIRCH_SIGN) || signState.isOf(Blocks.SPRUCE_SIGN)) {
                        var blockEntity= mc.getWorld().getBlockEntity(signPos);
                            if (blockEntity instanceof net.minecraft.block.entity.SignBlockEntity sign) {
                                StringBuilder sb= new StringBuilder();
                                for (net.minecraft.text.Text text : sign.getFrontText().getMessages(false)) {
                                    sb.append(text.getString()).append(" ");
                                }
                                if (normalize(sb.toString()).contains(keyword)) {
                                    return pos;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /** Найти ближайший верстак. */
    private BlockPos findCraftingTable(ClientPlayerEntity player) {
        BlockPos center= player.getBlockPos();
        int r= 10;
        BlockPos best= null;
        double bestDist= Double.MAX_VALUE;
        for (int x = -r; x <= r; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos= center.add(x, y, z);
                    if (mc.getWorld().getBlockState(pos).isOf(Blocks.CRAFTING_TABLE)) {
                        double d= player.getEntityPos().distanceTo(pos.toCenterPos());
                        if (d < bestDist) {
                            bestDist = d;
                            best = pos;
                        }
                    }
                }
            }
        }
        return best;
    }

    // ======================================================
    // ВСПОМОГАТЕЛЬНЫЕ
    // ======================================================

    private int countItem(ClientPlayerEntity player, net.minecraft.item.Item item) {
        int total= 0;
        for (int i = 0; i < 36; i++) {
            var stack= player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.isOf(item)) total += stack.getCount();
        }
        return total;
    }

    private void closeCurrent(ClientPlayerEntity player) {
        if (mc.getCurrentScreen() != null) {
            player.closeHandledScreen();
        }
    }

    private void handleChatPacket(String raw) {
        String msg= normalize(Formatting.strip(raw));
        if (msg == null) return;

        // Покупка изумрудов подтверждена
        if (currentPhase == Phase.WAIT && (msg.contains("куплено") || msg.contains("bought"))) {
            setPhase(Phase.TRADE_SELECT);
        }
        // Аукцион недоступен
        if (msg.contains("после входа на режим необходимо немного подождать")) {
            notify("Аукцион временно недоступен", NotificationType.ERROR);
            if (autoSellBlocks.isValue()) setPhase(Phase.TRADE_SELECT);
        }
        // Не в клане
        if (msg.contains("вы не состоите в клане")) {
            notify("Не в клане — выключаю Auto Trade", NotificationType.ERROR);
            switchState();
        }
        // Нет денег
        if (msg.contains("нет денег") || msg.contains("недостаточно средств")) {
            notify("Недостаточно средств для покупки изумрудов", NotificationType.ERROR);
            setPhase(Phase.TRADE_SELECT);
        }
        // Ресток жителя (звук)
        if (currentPhase == Phase.WAIT && (msg.contains("готов торговать") || msg.contains("пополнил") || msg.contains("restock"))) {
            setPhase(Phase.TRADE_SELECT);
        }
    }

    private void notify(String text, NotificationType type) {
        if (notifyTimer.hasElapsed(2500)) {
            Expensive.INSTANCE.notificationRepository().post(type, Text.literal(text), 3L, TimeUnit.SECONDS);
            notifyTimer.reset();
        }
    }

    private void setPhase(Phase phase) {
        this.currentPhase = phase;
        this.actionTimer.reset();
    }

    private String normalize(String s) {
        if (s == null) return "";
        return Formatting.strip(s == null ? "" : s).toLowerCase(Locale.ROOT).replace('ё', 'е').trim();
    }

    private void resetState() {
        currentPhase = Phase.IDLE;
        targetVillager = null;
        goldChestPos = null;
        craftTablePos = null;
        blacklistedChests.clear();
        retryCount = 0;
        tradesDone = 0;
        restockPosA = null;
        restockPosB = null;
        actionTimer.reset();
        cycleTimer.reset();
        notifyTimer.reset();
        restockTimer.reset();
        buyTimer.reset();
    }

    @Override
    public void deactivate() {
        resetState();
        super.deactivate();
    }
}
