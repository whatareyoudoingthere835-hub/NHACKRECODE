package aethereal.core.types;
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


public final class InventorySlotRef {
    public final int slot;
    public final InventoryScope scope;

    public InventorySlotRef(int i, InventoryScope class305Var) {
        this.slot = i;
        this.scope = class305Var;
    }

    public int increasedSlot() {
        return this.slot < 9 ? this.slot + 36 : this.slot;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "slot=" + this.slot + ", " + "scope=" + this.scope + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.slot, this.scope);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof InventorySlotRef)) return false;
        InventorySlotRef o= (InventorySlotRef) obj;
        return java.util.Objects.equals(this.slot, o.slot) && java.util.Objects.equals(this.scope, o.scope);
    }
public int slot() {
        return this.slot;
    }

    public InventoryScope scope() {
        return this.scope;
    }
}
