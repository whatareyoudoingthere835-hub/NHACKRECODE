package aethereal.system.config;
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

public class SilentSlotManager {
    public PendingSlotSwap pendingSwap;
    public final Mc mc = Mc.INSTANCE;
    public int tickCounter = 0;

    public int getServersideSlot() {
        if (this.pendingSwap != null && this.pendingSwap.pendingSlot != null) {
            return this.pendingSwap.pendingSlot.slot();
        }
        if (this.mc.getPlayer() != null) {
            return this.mc.getPlayer().getInventory().getSelectedSlot();
        }
        return 0;
    }

    public int getClientsideSlot() {
        if (this.pendingSwap != null) {
            return this.pendingSwap.clientsideSlot();
        }
        if (this.mc.getPlayer() != null) {
            return this.mc.getPlayer().getInventory().getSelectedSlot();
        }
        return 0;
    }

    public void update() {
        if (this.pendingSwap != null) {
            int i= this.tickCounter;
            this.tickCounter = i + 1;
            if (i >= this.pendingSwap.ticksUntilReset()) {
                this.pendingSwap = null;
            }
        }
    }

    public void swapTo(Object obj, InventorySlotRef class246Var, int i) {
        this.pendingSwap = new PendingSlotSwap(obj, class246Var, getClientsideSlot(), i);
        this.tickCounter = 0;
    }
}
