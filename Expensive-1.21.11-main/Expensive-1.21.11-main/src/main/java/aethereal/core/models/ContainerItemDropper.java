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

import net.minecraft.item.Item;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class ContainerItemDropper implements ItemDropper {
    @Override
    public void dropItems(ScreenHandler screenHandler, int i, Item item) {
        if (screenHandler instanceof GenericContainerScreenHandler) {
            GenericContainerScreenHandler genericContainerScreenHandler= (GenericContainerScreenHandler) screenHandler;
            int size= genericContainerScreenHandler.getInventory().size();
            for (int i2 = 0; i2 < size; i2++) {
                Slot slot= (Slot) genericContainerScreenHandler.slots.get(i2);
                if (!slot.getStack().isEmpty() && slot.getStack().getItem() == item) {
                    PlayerActionUtil.INSTANCE.windowClick(SlotActionType.THROW, i2, 1);
                }
            }
        }
    }
}
