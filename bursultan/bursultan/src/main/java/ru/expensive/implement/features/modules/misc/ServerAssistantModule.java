package ru.expensive.implement.features.modules.misc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BindSetting;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.api.feature.module.setting.implement.GroupSetting;
import ru.expensive.api.feature.module.setting.implement.MultiSelectSetting;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;
import ru.expensive.common.util.auction.AuctionPriceParser;
import ru.expensive.implement.events.container.HandledScreenEvent;
import ru.expensive.implement.events.item.TooltipEvent;
import ru.expensive.implement.events.keyboard.KeyEvent;
import ru.expensive.implement.events.packet.PacketEvent;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.mixins.accessors.ScreenHandlerSlotUpdateAccessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Помощник для FunTime / SpookyTime / HolyWorld: аукционный ассистент
 * (подсветка дешёвого лота, сортировка по цене), авто-божья аура,
 * бинды на расходники, цена за штуку в тултипе.
 */
public class ServerAssistantModule extends Module {
    private final SelectSetting targetServerSetting = new SelectSetting("Target Server", "Server for assistance features")
            .value("FunTime", "SpookyTime", "HolyWorld");
    private final MultiSelectSetting elementsSetting = new MultiSelectSetting("Elements", "Assistance elements")
            .value("Аукционный ассистент", "Сортировать по цене");
    private final BooleanSetting autoGodAuraSetting = new BooleanSetting("Auto God Aura", "Auto use god aura")
            .visible(() -> isFunTimeTarget() || isSpookyTarget());

    private final GroupSetting armorFilterSetting = new GroupSetting("Armor Filter", "Filter armor by")
            .settings(
                    new BooleanSetting("Защита", "Require protection").setValue(false),
                    new BooleanSetting("Аншип", "Without thorns").setValue(true),
                    new BooleanSetting("Починка", "Require mending").setValue(true),
                    new BooleanSetting("Подводная ходьба", "Require depth strider on boots").setValue(true))
            .visible(() -> elementsSetting.isSelected("Аукционный ассистент") && (isFunTimeTarget() || isSpookyTarget()));

    private final GroupSetting swordFilterSetting = new GroupSetting("Sword Filter", "Filter swords by")
            .settings(
                    new BooleanSetting("Острота", "Require sharpness").setValue(false),
                    new BooleanSetting("Детекция", "Require detection").setValue(true),
                    new BooleanSetting("Вампиризм", "Require vampirism").setValue(true),
                    new BooleanSetting("Окисление", "Require oxidation").setValue(true),
                    new BooleanSetting("Яд", "Require poison").setValue(true))
            .visible(() -> elementsSetting.isSelected("Аукционный ассистент") && (isFunTimeTarget() || isSpookyTarget()));

    private final GroupSetting pickaxeFilterSetting = new GroupSetting("Pickaxe Filter", "Filter pickaxes by")
            .settings(
                    new BooleanSetting("Эффективность", "Require efficiency").setValue(false),
                    new BooleanSetting("Удача", "Require fortune").setValue(true),
                    new BooleanSetting("Магнит", "Require magnet").setValue(true),
                    new BooleanSetting("Починка", "Require mending").setValue(true))
            .visible(() -> elementsSetting.isSelected("Аукционный ассистент") && (isFunTimeTarget() || isSpookyTarget()));

    public record ItemBind(BindSetting setting, Item item, List<String> servers) {
    }

    private final List<ItemBind> itemBinds = new ArrayList<>();
    private final AuctionPriceParser auctionPriceParser = new AuctionPriceParser();
    private final Map<Integer, int[]> sortMaps = new HashMap<>();
    private long lastGodAuraUse;

    public ServerAssistantModule() {
        super("ServerAssistant", "Server Assistant", ModuleCategory.MISC);
        elementsSetting.setSelected(new ArrayList<>(List.of("Аукционный ассистент", "Сортировать по цене")));
        armorFilterSetting.setValue(true);
        swordFilterSetting.setValue(true);
        pickaxeFilterSetting.setValue(true);

        List<BindSetting> binds = new ArrayList<>();
        binds.add(bindSetting("Ком снега", Items.SNOWBALL, "HolyWorld"));
        binds.add(bindSetting("Стан", Items.NETHER_STAR, "HolyWorld"));
        binds.add(bindSetting("Взрывная трапка", Items.PRISMARINE_SHARD, "HolyWorld"));
        binds.add(bindSetting("Взрывная палочка", Items.BLAZE_ROD, "HolyWorld"));
        binds.add(bindSetting("Взрывная штучка", Items.FIRE_CHARGE, "HolyWorld"));
        binds.add(bindSetting("Божья аура", Items.PHANTOM_MEMBRANE, "FunTime", "SpookyTime"));
        binds.add(bindSetting("Огненный заряд", Items.FIRE_CHARGE, "FunTime", "SpookyTime"));
        binds.add(bindSetting("Заряд ветра", Items.WIND_CHARGE, "FunTime", "SpookyTime"));
        binds.add(bindSetting("Явная пыль", Items.SUGAR, "FunTime", "SpookyTime"));
        binds.add(bindSetting("Дезориентация", Items.ENDER_EYE, "FunTime", "SpookyTime"));
        binds.add(bindSetting("Пласт", Items.DRIED_KELP, "FunTime", "SpookyTime"));
        binds.add(bindSetting("Снежок заморозка", Items.SNOWBALL, "FunTime", "SpookyTime"));
        binds.add(bindSetting("Трапка", Items.NETHERITE_SCRAP, "FunTime", "SpookyTime"));

        List<ru.expensive.api.feature.module.setting.Setting> settings = new ArrayList<>();
        settings.add(targetServerSetting);
        settings.add(elementsSetting);
        settings.add(armorFilterSetting);
        settings.add(swordFilterSetting);
        settings.add(pickaxeFilterSetting);
        settings.addAll(binds);
        settings.add(autoGodAuraSetting);
        setup(settings.toArray(new ru.expensive.api.feature.module.setting.Setting[0]));
    }

    public List<ItemBind> getItemBinds() {
        return itemBinds;
    }

    private BindSetting bindSetting(String name, Item item, String... servers) {
        List<String> serverList = List.of(servers);
        BindSetting setting = new BindSetting(name, "Use " + name)
                .visible(() -> serverList.stream().anyMatch(targetServerSetting::isSelected));
        itemBinds.add(new ItemBind(setting, item, serverList));
        return setting;
    }

    private boolean isFunTimeTarget() {
        return targetServerSetting.isSelected("FunTime");
    }

    private boolean isSpookyTarget() {
        return targetServerSetting.isSelected("SpookyTime");
    }

    private static boolean isOnServer(String fragment) {
        return mcStatic().getCurrentServerEntry() != null
                && mcStatic().getCurrentServerEntry().address.toLowerCase(Locale.US).contains(fragment);
    }

    private static net.minecraft.client.MinecraftClient mcStatic() {
        return net.minecraft.client.MinecraftClient.getInstance();
    }

    private boolean isFunTimeActive() {
        return isFunTimeTarget() && isOnServer("funtime");
    }

    private boolean isSpookyActive() {
        return isSpookyTarget() && isOnServer("spooky");
    }

    // ---------- Авто-божья аура ----------

    @EventHandler
    public void onTick(TickEvent event) {
        if (!autoGodAuraSetting.isValue() || !(isFunTimeActive() || isSpookyActive())) {
            return;
        }
        if (mc.player == null || mc.world == null) {
            return;
        }
        boolean hasBadEffect = mc.player.getStatusEffects().stream().anyMatch(effect ->
                effect.getEffectType() == StatusEffects.WEAKNESS
                        && effect.getAmplifier() >= 1
                        && effect.getDuration() / 20.0 >= 15.0);
        if (!hasBadEffect) {
            return;
        }
        if (findInventorySlot(Items.PHANTOM_MEMBRANE) == -1) {
            return;
        }
        if (mc.player.getItemCooldownManager().isCoolingDown(Items.PHANTOM_MEMBRANE.getDefaultStack())) {
            return;
        }
        if (mc.player.getAbsorptionAmount() > 3.0F) {
            return;
        }
        if (System.currentTimeMillis() - lastGodAuraUse < 5000L) {
            return;
        }
        if (useItem(Items.PHANTOM_MEMBRANE)) {
            lastGodAuraUse = System.currentTimeMillis();
        }
    }

    // ---------- Бинды на предметы ----------

    @EventHandler
    public void onKey(KeyEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        for (ItemBind bind : itemBinds) {
            int key = bind.setting().getKey();
            if (key < 0 || !event.isKeyDown(key)) {
                continue;
            }
            if (bind.servers().stream().noneMatch(targetServerSetting::isSelected)) {
                continue;
            }
            useBindItem(bind);
        }
    }

    private void useBindItem(ItemBind bind) {
        String name = bind.setting().getName();
        Item item = bind.item();
        if (mc.player.getItemCooldownManager().isCoolingDown(item.getDefaultStack())) {
            logDirect(name + " - имеет задержку", Formatting.RED);
            return;
        }
        if (findInventorySlot(item) == -1) {
            logDirect(name + " - нет в инвентаре", Formatting.RED);
            return;
        }
        useItem(item);
    }

    private int findInventorySlot(Item item) {
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    private boolean useItem(Item item) {
        if (mc.player == null || mc.interactionManager == null) {
            return false;
        }
        if (mc.player.currentScreenHandler.syncId != 0) {
            return false;
        }
        int slot = findInventorySlot(item);
        if (slot == -1) {
            return false;
        }
        int selected = mc.player.getInventory().getSelectedSlot();
        if (slot != selected) {
            mc.interactionManager.clickSlot(0, slot, selected, SlotActionType.SWAP, mc.player);
        }
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        if (slot != selected) {
            mc.interactionManager.clickSlot(0, slot, selected, SlotActionType.SWAP, mc.player);
        }
        return true;
    }

    // ---------- Цена за штуку в тултипе ----------

    @EventHandler
    public void onTooltip(TooltipEvent event) {
        if (!(isFunTimeActive() || isSpookyActive())) {
            return;
        }
        ItemStack stack = event.getStack();
        if (stack.getCount() <= 1) {
            return;
        }
        int price = auctionPriceParser.getPrice(stack);
        if (price <= 0) {
            return;
        }
        List<Text> lines = event.getLines();
        for (int i = 0; i < lines.size(); i++) {
            // В лоре FunTime буквально "$ Ценa: " (с латинской 'a').
            if (lines.get(i).getString().contains("$ Ценa: ")) {
                Text line = Text.literal("$").formatted(Formatting.GREEN)
                        .append(Text.literal(" Цена за штуку: ").formatted(Formatting.WHITE))
                        .append(Text.literal(String.format(Locale.US, "%,d", price)).formatted(Formatting.GREEN));
                lines.add(i + 1, line);
                return;
            }
        }
    }

    // ---------- Подсветка дешёвого лота ----------

    @EventHandler
    public void onHandledScreen(HandledScreenEvent event) {
        if (!(isFunTimeActive() || isSpookyActive())) {
            return;
        }
        if (!elementsSetting.isSelected("Аукционный ассистент")) {
            return;
        }
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
            return;
        }
        List<Slot> slots = screen.getScreenHandler().slots;
        if (slots.size() < 90) {
            return;
        }
        List<Slot> containerSlots = slots.subList(0, slots.size() - 36);
        boolean useFilter = containerSlots.stream().anyMatch(slot ->
                auctionPriceParser.getPrice(slot.getStack()) >= 0 && passesFilter(slot.getStack()));

        Slot cheapest = null;
        int minPrice = Integer.MAX_VALUE;
        for (Slot slot : containerSlots) {
            ItemStack stack = slot.getStack();
            int value = auctionPriceParser.getPrice(stack);
            if ((!useFilter || passesFilter(stack)) && value >= 0 && value < minPrice) {
                minPrice = value;
                cheapest = slot;
            }
        }
        if (cheapest == null) {
            return;
        }

        int offsetX = (screen.width - event.getBackgroundWidth()) / 2;
        int offsetY = (screen.height - event.getBackgroundHeight()) / 2;
        float pulse = (float) ((Math.sin(System.currentTimeMillis() / 1000.0 * 10.0) + 1.0) * 0.5);
        int color = ((((int) (25.0F + 175.0F * pulse)) & 0xFF) << 24) | 0x00FF00;
        event.getDrawContext().fill(offsetX + cheapest.x, offsetY + cheapest.y,
                offsetX + cheapest.x + 16, offsetY + cheapest.y + 16, color);
    }

    // ---------- Сортировка по цене ----------

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.isReceive()) {
            if (event.getPacket() instanceof InventoryS2CPacket packet) {
                handleInventory(packet);
            } else if (event.getPacket() instanceof ScreenHandlerSlotUpdateS2CPacket packet) {
                int[] map = sortMaps.get(packet.getSyncId());
                if (map != null && packet.getSyncId() != 0 && mc.player != null
                        && packet.getSyncId() == mc.player.currentScreenHandler.syncId
                        && packet.getSlot() >= 0 && packet.getSlot() < map.length) {
                    for (int display = 0; display < map.length; display++) {
                        if (map[display] == packet.getSlot()) {
                            ((ScreenHandlerSlotUpdateAccessor) packet).setSlot(display);
                            break;
                        }
                    }
                }
            }
        } else if (event.isSend() && event.getPacket() instanceof ClickSlotC2SPacket packet) {
            int[] map = sortMaps.get(packet.syncId());
            if (map != null && packet.syncId() != 0 && mc.player != null
                    && packet.syncId() == mc.player.currentScreenHandler.syncId
                    && packet.slot() >= 0 && packet.slot() < map.length) {
                int real = map[packet.slot()];
                Int2ObjectMap<ItemStackHash> remapped = new Int2ObjectOpenHashMap<>();
                for (Int2ObjectMap.Entry<ItemStackHash> entry : packet.modifiedStacks().int2ObjectEntrySet()) {
                    int key = entry.getIntKey();
                    remapped.put((key >= 0 && key < map.length) ? map[key] : key, entry.getValue());
                }
                ClickSlotC2SPacket replacement = new ClickSlotC2SPacket(
                        packet.syncId(), packet.revision(), (short) real,
                        packet.button(), packet.actionType(), remapped, packet.cursor());
                event.cancel();
                mc.getNetworkHandler().sendPacket(replacement);
            }
        }
    }

    private void handleInventory(InventoryS2CPacket packet) {
        if (packet.syncId() == 0) {
            sortMaps.clear();
            return;
        }
        if (!(isFunTimeActive() || isSpookyActive())) {
            return;
        }
        if (!elementsSetting.isSelected("Сортировать по цене")) {
            return;
        }
        List<ItemStack> contents = packet.contents();
        int chestSlots = contents.size() > 36 ? contents.size() - 36 : contents.size();
        if (chestSlots < 44) {
            sortMaps.remove(packet.syncId());
            return;
        }
        boolean useFilter = contents.subList(0, chestSlots).stream().anyMatch(stack ->
                auctionPriceParser.getPrice(stack) >= 0 && passesFilter(stack));

        int[] prices = new int[chestSlots];
        for (int i = 0; i < chestSlots; i++) {
            ItemStack stack = contents.get(i);
            int value = (stack.isEmpty() || (useFilter && !passesFilter(stack)))
                    ? -1 : auctionPriceParser.getPrice(stack);
            prices[i] = value < 0 ? Integer.MAX_VALUE : value;
        }
        Integer[] order = IntStream.range(0, chestSlots).boxed()
                .sorted(Comparator.comparingInt(i -> prices[i]))
                .toArray(Integer[]::new);

        List<ItemStack> original = new ArrayList<>(contents);
        int[] map = new int[chestSlots];
        for (int display = 0; display < chestSlots; display++) {
            map[display] = order[display];
            contents.set(display, original.get(order[display]));
        }
        sortMaps.put(packet.syncId(), map);
    }

    // ---------- Фильтры предметов ----------

    private boolean passesFilter(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        // В 1.21.11 классов SwordItem/PickaxeItem/ArmorItem нет — предметы
        // data-driven: тип определяем по тегам и компонентам.
        if (stack.contains(DataComponentTypes.EQUIPPABLE)) {
            return armorPasses(stack);
        }
        if (stack.isIn(ItemTags.SWORDS)) {
            return swordPasses(stack);
        }
        if (stack.isIn(ItemTags.PICKAXES)) {
            return pickaxePasses(stack);
        }
        return true;
    }

    private boolean armorSubSetting(String name) {
        return ((BooleanSetting) armorFilterSetting.getSubSetting(name)).isValue();
    }

    private boolean swordSubSetting(String name) {
        return ((BooleanSetting) swordFilterSetting.getSubSetting(name)).isValue();
    }

    private boolean pickaxeSubSetting(String name) {
        return ((BooleanSetting) pickaxeFilterSetting.getSubSetting(name)).isValue();
    }

    private boolean armorPasses(ItemStack stack) {
        if (armorSubSetting("Защита")) {
            if (enchantLevel(stack, Enchantments.UNBREAKING) < 4) return false;
            if (enchantLevel(stack, Enchantments.PROTECTION) < 5) return false;
        }
        if (armorSubSetting("Аншип") && enchantLevel(stack, Enchantments.THORNS) >= 1) return false;
        if (armorSubSetting("Починка") && enchantLevel(stack, Enchantments.MENDING) < 1) return false;
        if (armorSubSetting("Подводная ходьба") && isBoots(stack)
                && enchantLevel(stack, Enchantments.DEPTH_STRIDER) < 1) return false;
        return true;
    }

    private boolean swordPasses(ItemStack stack) {
        if (enchantLevel(stack, Enchantments.KNOCKBACK) >= 2) return false;
        if (swordSubSetting("Острота") && enchantLevel(stack, Enchantments.SHARPNESS) < 6) return false;
        if (enchantLevel(stack, Enchantments.KNOCKBACK) >= 1) return false;

        DescriptionChecker checker = new DescriptionChecker()
                .blacklist("Нестабильность ", "Нестабильный ");
        if (swordSubSetting("Детекция")) checker.require("Детекция", 2);
        if (swordSubSetting("Вампиризм")) checker.require("Вампиризм", 2);
        if (swordSubSetting("Окисление")) checker.require("Окисление", 2);
        if (swordSubSetting("Яд")) checker.require("Яд", 3);
        return checker.test(stack);
    }

    private boolean pickaxePasses(ItemStack stack) {
        if (pickaxeSubSetting("Починка") && enchantLevel(stack, Enchantments.MENDING) < 1) return false;

        DescriptionChecker checker = new DescriptionChecker();
        if (pickaxeSubSetting("Удача")) checker.require("Удача", 5);
        if (pickaxeSubSetting("Эффективность")) checker.require("Эффективность", 4);
        if (pickaxeSubSetting("Магнит")) checker.require("Магнит", 1);
        return checker.test(stack);
    }

    private static boolean isBoots(ItemStack stack) {
        return stack.get(DataComponentTypes.EQUIPPABLE) != null
                && stack.get(DataComponentTypes.EQUIPPABLE).slot() == EquipmentSlot.FEET;
    }

    private static int enchantLevel(ItemStack stack, RegistryKey<Enchantment> key) {
        for (RegistryEntry<Enchantment> entry : stack.getEnchantments().getEnchantments()) {
            if (entry.getKey().map(key::equals).orElse(false)) {
                return stack.getEnchantments().getLevel(entry);
            }
        }
        return 0;
    }

    private static List<String> loreLines(ItemStack stack) {
        if (stack.get(DataComponentTypes.LORE) == null) {
            return List.of();
        }
        List<String> lines = new ArrayList<>();
        stack.get(DataComponentTypes.LORE).lines().forEach(line -> lines.add(line.getString()));
        return lines;
    }

    private static class DescriptionChecker {
        private final List<String> blacklist = new ArrayList<>();
        private final Map<String, Integer> requires = new HashMap<>();

        DescriptionChecker blacklist(String... parts) {
            blacklist.addAll(List.of(parts));
            return this;
        }

        DescriptionChecker require(String name, int minLevel) {
            requires.put(name, minLevel);
            return this;
        }

        boolean test(ItemStack stack) {
            List<String> lines = loreLines(stack);
            for (String banned : blacklist) {
                for (String line : lines) {
                    if (line.contains(banned)) {
                        return false;
                    }
                }
            }
            for (Map.Entry<String, Integer> required : requires.entrySet()) {
                boolean ok = false;
                for (String line : lines) {
                    if (parseLevel(line, required.getKey()) >= required.getValue()) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    return false;
                }
            }
            return true;
        }

        private static int parseLevel(String line, String name) {
            int index = line.indexOf(name);
            if (index < 0) {
                return -1;
            }
            String rest = line.substring(index + name.length()).replaceAll("[^IVXLCDM0-9]", "");
            if (rest.isEmpty()) {
                return 1;
            }
            try {
                return Integer.parseInt(rest);
            } catch (NumberFormatException ignored) {
                return romanToInt(rest);
            }
        }

        private static int romanToInt(String roman) {
            int result = 0, prev = 0;
            for (int i = roman.length() - 1; i >= 0; i--) {
                int current = switch (roman.charAt(i)) {
                    case 'I' -> 1;
                    case 'V' -> 5;
                    case 'X' -> 10;
                    case 'L' -> 50;
                    case 'C' -> 100;
                    case 'D' -> 500;
                    case 'M' -> 1000;
                    default -> 0;
                };
                if (current < prev) {
                    result -= current;
                } else {
                    result += current;
                }
                prev = current;
            }
            return result;
        }
    }
}
