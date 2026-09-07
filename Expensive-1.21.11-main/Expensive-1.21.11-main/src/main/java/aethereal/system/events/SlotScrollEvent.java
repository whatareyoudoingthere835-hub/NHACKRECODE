package aethereal.system.events;
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

import net.minecraft.screen.slot.Slot;

public final class SlotScrollEvent implements Event {
    public final Slot slot;
    public final int slotId;

    public SlotScrollEvent(Slot slot, int i) {
        this.slot = slot;
        this.slotId = i;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "slot=" + this.slot + ", " + "slotId=" + this.slotId + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.slot, this.slotId);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SlotScrollEvent)) return false;
        SlotScrollEvent o= (SlotScrollEvent) obj;
        return java.util.Objects.equals(this.slot, o.slot) && java.util.Objects.equals(this.slotId, o.slotId);
    }
public Slot slot() {
        return this.slot;
    }

    public int slotId() {
        return this.slotId;
    }
}
