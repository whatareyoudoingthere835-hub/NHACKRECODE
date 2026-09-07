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

public final class SlotSearchResult2 {
    public final InventorySlotRef slotReference;
    public final ItemStack stack;

    public SlotSearchResult2(InventorySlotRef class246Var, ItemStack itemStack) {
        this.slotReference = class246Var;
        this.stack = itemStack;
    }

    public boolean found() {
        return this.slotReference != null;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "slotReference=" + this.slotReference + ", " + "stack=" + this.stack + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.slotReference, this.stack);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SlotSearchResult2)) return false;
        SlotSearchResult2 o= (SlotSearchResult2) obj;
        return java.util.Objects.equals(this.slotReference, o.slotReference) && java.util.Objects.equals(this.stack, o.stack);
    }
public InventorySlotRef slotReference() {
        return this.slotReference;
    }

    public ItemStack stack() {
        return this.stack;
    }
}
