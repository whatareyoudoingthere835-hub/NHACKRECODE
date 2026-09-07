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

import net.minecraft.screen.slot.SlotActionType;

public class SlotClickEvent extends CancellableEvent {
    public int windowId;
    public int slotId;
    public int button;
    public SlotActionType actionType;

    public int getWindowId() {
        return this.windowId;
    }

    public int getSlotId() {
        return this.slotId;
    }

    public int getButton() {
        return this.button;
    }

    public SlotActionType getActionType() {
        return this.actionType;
    }

    public void setWindowId(int i) {
        this.windowId = i;
    }

    public void setSlotId(int i) {
        this.slotId = i;
    }

    public void setButton(int i) {
        this.button = i;
    }

    public void setActionType(SlotActionType slotActionType) {
        this.actionType = slotActionType;
    }

    public SlotClickEvent(int i, int i2, int i3, SlotActionType slotActionType) {
        this.windowId = i;
        this.slotId = i2;
        this.button = i3;
        this.actionType = slotActionType;
    }
}
