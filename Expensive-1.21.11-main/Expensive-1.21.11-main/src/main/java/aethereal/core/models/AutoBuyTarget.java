package aethereal.core.models;
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

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AutoBuyTarget {
    private ItemStack stack;
    private String name;
    private boolean enabled;
    private String priceText;
    private String sellPriceText;
    private int minCount;
    private int minDurability;
    private int sellLotSize;
    private boolean buyThorns;
    private boolean onlyOriginal;
    private Map<String, Integer> requiredEnchantments = new HashMap<>();

    public AutoBuyTarget(ItemStack stack, String name, boolean enabled, String priceText, int minCount, int minDurability) {
        this.stack = stack == null ? ItemStack.EMPTY : stack.copy();
        this.name = (name == null || name.isBlank()) ? this.stack.getName().getString() : name;
        this.enabled = enabled;
        this.priceText = priceText == null ? "" : priceText.trim();
        this.sellPriceText = "";
        this.minCount = Math.max(1, minCount);
        this.minDurability = Math.max(0, minDurability);
        this.sellLotSize = Math.max(1, this.stack.getMaxCount());

        extractEnchantments();
    }

    private void extractEnchantments() {
        if (this.stack.isEmpty()) return;
        ItemEnchantmentsComponent enchants= this.stack.get(DataComponentTypes.ENCHANTMENTS);
        if (enchants != null) {
            for (var entry : enchants.getEnchantmentEntries()) {
                String id= entry.getKey().getKey().map(k -> k.getValue().toString()).orElse("");
                if (!id.isEmpty()) {
                    this.requiredEnchantments.put(id, entry.getIntValue());
                }
            }
        }
    }

    public static long parseMoney(String str) {
        if (str == null) return 0L;
        String s= str.trim().toLowerCase(Locale.ROOT).replace("₽", "").replace("$", "").replace(" ", "").replace("_", "").replace(",", ".");
        if (s.isEmpty()) return 0L;
        double mult= 1.0d;
        char last= s.charAt(s.length() - 1);
        if (last == 'k' || last == 'к') {
            mult = 1000.0d;
            s = s.substring(0, s.length() - 1);
        } else if (last == 'm' || last == 'м') {
            mult = 1000000.0d;
            s = s.substring(0, s.length() - 1);
        } else if (last == 'b' || last == 'б') {
            mult = 1000000000.0d;
            s = s.substring(0, s.length() - 1);
        }
        try {
            double val= Double.parseDouble(s) * mult;
            if (!Double.isFinite(val) || val <= 0.0d) return 0L;
            return Math.min(Long.MAX_VALUE, Math.round(val));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static String money(long amount) {
        if (amount <= 0) return "0$";
        return new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US)).format(amount).replace(',', ' ') + "$";
    }

    public boolean matches(ItemStack otherStack, long price) {
        if (!enabled || otherStack.isEmpty()) return false;
        if (!otherStack.isOf(this.stack.getItem())) return false;
        if (otherStack.getCount() < minCount) return false;

        long maxPrice= parseMoney(this.priceText);
        if (maxPrice > 0 && price > maxPrice) return false;

        if (minDurability > 0 && otherStack.isDamageable()) {
            int remaining= otherStack.getMaxDamage() - otherStack.getDamage();
            int percent= (int) ((remaining / (double) otherStack.getMaxDamage()) * 100.0);
            if (percent < minDurability) return false;
        }

        // Check Thorns filter (if buyThorns == false, reject items with Thorns)
        if (!buyThorns) {
            ItemEnchantmentsComponent enchants= otherStack.get(DataComponentTypes.ENCHANTMENTS);
            if (enchants != null) {
                for (var entry : enchants.getEnchantmentEntries()) {
                    String id= entry.getKey().getKey().map(k -> k.getValue().toString()).orElse("");
                    if (id.contains("thorns") || id.contains("шипы")) {
                        return false;
                    }
                }
            }
        }

        // Check required standard enchantments
        if (!requiredEnchantments.isEmpty()) {
            ItemEnchantmentsComponent otherEnchants= otherStack.get(DataComponentTypes.ENCHANTMENTS);
            if (otherEnchants == null) return false;
            for (Map.Entry<String, Integer> entry : requiredEnchantments.entrySet()) {
                boolean found= false;
                for (var otherEntry : otherEnchants.getEnchantmentEntries()) {
                    String otherId= otherEntry.getKey().getKey().map(k -> k.getValue().toString()).orElse("");
                    if (otherId.equals(entry.getKey()) && otherEntry.getIntValue() >= entry.getValue()) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }

        return true;
    }

    public boolean equalParameters(AutoBuyTarget other) {
        if (other == null) return false;
        return this.stack.getItem().equals(other.stack.getItem()) && this.name.equals(other.name);
    }

    // Getters and Setters
    public ItemStack getStack() { return stack; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getPriceText() { return priceText; }
    public void setPriceText(String priceText) { this.priceText = priceText; }
    public String getSellPriceText() { return sellPriceText; }
    public void setSellPriceText(String sellPriceText) { this.sellPriceText = sellPriceText; }
    public int getMinCount() { return minCount; }
    public void setMinCount(int minCount) { this.minCount = minCount; }
    public int getMinDurability() { return minDurability; }
    public void setMinDurability(int minDurability) { this.minDurability = minDurability; }
    public int getSellLotSize() { return sellLotSize; }
    public void setSellLotSize(int sellLotSize) { this.sellLotSize = sellLotSize; }
    public boolean isBuyThorns() { return buyThorns; }
    public void setBuyThorns(boolean buyThorns) { this.buyThorns = buyThorns; }
    public boolean isOnlyOriginal() { return onlyOriginal; }
    public void setOnlyOriginal(boolean onlyOriginal) { this.onlyOriginal = onlyOriginal; }
    public Map<String, Integer> getRequiredEnchantments() { return requiredEnchantments; }
}
