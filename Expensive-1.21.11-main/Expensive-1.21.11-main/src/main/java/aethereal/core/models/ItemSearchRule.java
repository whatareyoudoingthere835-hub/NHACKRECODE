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

import java.util.function.Predicate;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class ItemSearchRule {
    public final boolean serverItem;
    public final String serverName;
    public final Item boundItem;
    public final boolean needRotation;

    public ItemSearchRule(boolean z, String str, Item item, boolean z2) {
        this.serverItem = z;
        this.serverName = str;
        this.boundItem = item;
        this.needRotation = z2;
    }

    public Predicate<ItemStack> getSearchPredicate() {
        if (this.boundItem == Items.CROSSBOW) {
            return itemStack -> {
                return itemStack.getItem() == this.boundItem && CrossbowItem.isCharged(itemStack);
            };
        }
        return this.serverName != null ? itemStack2 -> {
            return itemStack2.getItem() == this.boundItem && itemStack2.getName().getString().toLowerCase().contains(this.serverName);
        } : itemStack3 -> {
            return itemStack3.getItem() == this.boundItem;
        };
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "serverItem=" + this.serverItem + ", " + "serverName=" + this.serverName + ", " + "boundItem=" + this.boundItem + ", " + "needRotation=" + this.needRotation + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.serverItem, this.serverName, this.boundItem, this.needRotation);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemSearchRule)) return false;
        ItemSearchRule o= (ItemSearchRule) obj;
        return java.util.Objects.equals(this.serverItem, o.serverItem) && java.util.Objects.equals(this.serverName, o.serverName) && java.util.Objects.equals(this.boundItem, o.boundItem) && java.util.Objects.equals(this.needRotation, o.needRotation);
    }
public boolean serverItem() {
        return this.serverItem;
    }

    public String serverName() {
        return this.serverName;
    }

    public Item boundItem() {
        return this.boundItem;
    }

    public boolean needRotation() {
        return this.needRotation;
    }
}
