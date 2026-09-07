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

public class PendingSlotSwap {
    public Object owner;
    public InventorySlotRef pendingSlot;
    public int clientsideSlot;
    public int ticksUntilReset;

    public PendingSlotSwap() {
    }

    public PendingSlotSwap(Object obj, InventorySlotRef class246Var, int i, int i2) {
        this.owner = obj;
        this.pendingSlot = class246Var;
        this.clientsideSlot = i;
        this.ticksUntilReset = i2;
    }

    public InventorySlotRef pendingSlot() {
        return this.pendingSlot;
    }

    public int clientsideSlot() {
        return this.clientsideSlot;
    }

    public int ticksUntilReset() {
        return this.ticksUntilReset;
    }
}
