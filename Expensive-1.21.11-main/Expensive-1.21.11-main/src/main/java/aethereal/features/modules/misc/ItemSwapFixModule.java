package aethereal.features.modules.misc;
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

import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

@Aliases(aliases = {"Item Swap Fix", "Swap Correction", "Slot Fix", "Item Slot Manager", "Swap Bug Fix", "Item Switch Fix", "Fix Slot Swap", "No Swap Fix", "No Item Swap", "Hotbar Fix", "Hotbar Sync"})
public class ItemSwapFixModule extends Module {
    public ItemSwapFixModule() {
        super(ModuleTab.MISC, "Item Swap Fix");
        register(PacketReceiveEvent.class, class051Var -> {
            Mc class815Var= Mc.INSTANCE;
            if (isState() && class815Var.isWorldLoaded()) {
                if ((class051Var.getPacket()) instanceof UpdateSelectedSlotS2CPacket packet ) {
                    try {
                        if (packet.slot() != class815Var.getPlayer().getInventory().getSelectedSlot()) {
                            class051Var.cancel();
                        }
                    } catch (Throwable th) {
                        throw new MatchException(th.toString(), th);
                    }
                }
            }
        });
    }
}
