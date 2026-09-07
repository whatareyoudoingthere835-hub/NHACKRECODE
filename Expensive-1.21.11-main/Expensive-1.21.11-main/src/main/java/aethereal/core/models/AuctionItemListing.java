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
import net.minecraft.screen.slot.Slot;

public final class AuctionItemListing {
    public final int price;
    public final Slot slot;
    public final ItemStack itemStack;

    public AuctionItemListing(int i, Slot slot, ItemStack itemStack) {
        this.price = i;
        this.slot = slot;
        this.itemStack = itemStack;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "price=" + this.price + ", " + "slot=" + this.slot + ", " + "itemStack=" + this.itemStack + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.price, this.slot, this.itemStack);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AuctionItemListing)) return false;
        AuctionItemListing o= (AuctionItemListing) obj;
        return java.util.Objects.equals(this.price, o.price) && java.util.Objects.equals(this.slot, o.slot) && java.util.Objects.equals(this.itemStack, o.itemStack);
    }
public int price() {
        return this.price;
    }

    public Slot slot() {
        return this.slot;
    }

    public ItemStack itemStack() {
        return this.itemStack;
    }
}
