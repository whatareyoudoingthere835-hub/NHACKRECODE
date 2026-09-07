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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ItemMatcher {
    public final Predicate<ItemStack> predicate;
    public final Item displayItem;

    public ItemMatcher(Predicate<ItemStack> predicate, Item item) {
        this.predicate = predicate;
        this.displayItem = item;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "predicate=" + this.predicate + ", " + "displayItem=" + this.displayItem + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.predicate, this.displayItem);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemMatcher)) return false;
        ItemMatcher o= (ItemMatcher) obj;
        return java.util.Objects.equals(this.predicate, o.predicate) && java.util.Objects.equals(this.displayItem, o.displayItem);
    }
public Predicate<ItemStack> predicate() {
        return this.predicate;
    }

    public Item displayItem() {
        return this.displayItem;
    }
}
