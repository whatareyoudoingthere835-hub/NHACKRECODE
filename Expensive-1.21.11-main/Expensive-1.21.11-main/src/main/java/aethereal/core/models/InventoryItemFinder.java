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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class InventoryItemFinder {
    public Optional<SlotSearchResult2> findItem(Predicate<ItemStack> predicate, InventoryScope... class305VarArr) {
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        if (player == null) {
            return Optional.empty();
        }
        for (InventoryScope class305Var : normalizeScopes(class305VarArr)) {
            for (int iStart = class305Var.start(); iStart <= class305Var.end(); iStart++) {
                ItemStack stack= player.getInventory().getStack(iStart);
                if (predicate.test(stack)) {
                    return Optional.of(new SlotSearchResult2(new InventorySlotRef(iStart, getScopeForSlot(iStart)), stack));
                }
            }
        }
        return Optional.empty();
    }

    public List<SlotSearchResult2> findAllItems(Predicate<ItemStack> predicate, InventoryScope... class305VarArr) {
        ArrayList arrayList= new ArrayList();
        ClientPlayerEntity player= Mc.INSTANCE.getPlayer();
        if (player == null) {
            return arrayList;
        }
        for (InventoryScope class305Var : normalizeScopes(class305VarArr)) {
            for (int iStart = class305Var.start(); iStart <= class305Var.end(); iStart++) {
                ItemStack stack= player.getInventory().getStack(iStart);
                if (predicate.test(stack)) {
                    arrayList.add(new SlotSearchResult2(new InventorySlotRef(iStart, getScopeForSlot(iStart)), stack));
                }
            }
        }
        return arrayList;
    }

    public static InventoryScope[] normalizeScopes(InventoryScope[] class305VarArr) {
        return class305VarArr.length == 0 ? new InventoryScope[]{InventoryScope.ALL} : class305VarArr;
    }

    public static InventoryScope getScopeForSlot(int i) {
        if (i >= InventoryScope.HOTBAR.start() && i <= InventoryScope.HOTBAR.end()) {
            return InventoryScope.HOTBAR;
        }
        if (i >= InventoryScope.INVENTORY.start() && i <= InventoryScope.INVENTORY.end()) {
            return InventoryScope.INVENTORY;
        }
        if (i < InventoryScope.ARMOR.start() || i > InventoryScope.ARMOR.end()) {
            return (i < InventoryScope.OFFHAND.start() || i > InventoryScope.OFFHAND.end()) ? InventoryScope.ALL : InventoryScope.OFFHAND;
        }
        return InventoryScope.ARMOR;
    }
}
