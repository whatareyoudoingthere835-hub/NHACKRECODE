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

import net.minecraft.item.ItemStack;

public final class PricedItemStack {
    public final int price;
    public final ItemStack stack;

    public PricedItemStack(int i, ItemStack itemStack) {
        this.price = i;
        this.stack = itemStack;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "price=" + this.price + ", " + "stack=" + this.stack + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.price, this.stack);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PricedItemStack)) return false;
        PricedItemStack o= (PricedItemStack) obj;
        return java.util.Objects.equals(this.price, o.price) && java.util.Objects.equals(this.stack, o.stack);
    }
public int price() {
        return this.price;
    }

    public ItemStack stack() {
        return this.stack;
    }
}
