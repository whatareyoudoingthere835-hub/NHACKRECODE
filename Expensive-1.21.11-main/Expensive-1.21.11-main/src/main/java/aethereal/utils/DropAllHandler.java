package aethereal.utils;
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

import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class DropAllHandler implements ClientHandler {
    public ItemDropper dropper;

    public DropAllHandler() {
        Expensive.INSTANCE.eventDispatcher().register(FocusedSlotEvent.class, class183Var -> {
            ScreenHandler screenHandler;
            Slot slotFocusedSlot= class183Var.focusedSlot();
            if (!isHotkeyPressed() || slotFocusedSlot == null || slotFocusedSlot.getStack() == null || (screenHandler = Mc.INSTANCE.getPlayer().currentScreenHandler) == null) {
                return;
            }
            selectDropper(screenHandler);
            if (this.dropper != null) {
                this.dropper.dropItems(screenHandler, slotFocusedSlot.id, slotFocusedSlot.getStack().getItem());
            }
        });
    }

    public void selectDropper(ScreenHandler screenHandler) {
        if (screenHandler instanceof GenericContainerScreenHandler) {
            this.dropper = new ContainerDropStrategy();
        } else if (screenHandler instanceof PlayerScreenHandler) {
            this.dropper = new PlayerInventoryDropper();
        } else {
            this.dropper = null;
        }
    }

    public boolean isHotkeyPressed() {
        return KeyboardUtil.isKeyPressed(341) && KeyboardUtil.isKeyPressed(340) && KeyboardUtil.isKeyPressed(81);
    }
}
