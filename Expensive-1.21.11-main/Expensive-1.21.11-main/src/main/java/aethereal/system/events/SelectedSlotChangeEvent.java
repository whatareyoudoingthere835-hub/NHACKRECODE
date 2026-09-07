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


public final class SelectedSlotChangeEvent implements Event {
    public final int newSelectedSlot;

    public SelectedSlotChangeEvent(int i) {
        this.newSelectedSlot = i;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "newSelectedSlot=" + this.newSelectedSlot + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.newSelectedSlot);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SelectedSlotChangeEvent)) return false;
        SelectedSlotChangeEvent o= (SelectedSlotChangeEvent) obj;
        return java.util.Objects.equals(this.newSelectedSlot, o.newSelectedSlot);
    }
public int newSelectedSlot() {
        return this.newSelectedSlot;
    }
}
