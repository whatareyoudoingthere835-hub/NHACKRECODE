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

import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

@Aliases(aliases = {"Inventory Tweaks", "Inventory Manager", "Inventory Expansion", "Keep Inventory Open", "Advanced Inventory", "InventoryPlus", "X Carry", "No Close Inventory", "Persistent Inventory"})
public class InventoryPlusModule extends Module {
    public InventoryPlusModule() {
        super(ModuleTab.MISC, "Inventory Plus");
        register(PacketSendEvent.class, class037Var -> {
            if (isState() && Mc.INSTANCE.isWorldLoaded() && (class037Var.getPacket() instanceof CloseHandledScreenC2SPacket)) {
                class037Var.cancel();
            }
        });
    }
}
