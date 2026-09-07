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

import net.minecraft.util.Hand;

public class ItemUseController {
    public static final ItemUseController INSTANCE = new ItemUseController();
    public boolean useItem;

    public void useHand(Hand hand) {
        Mc class815Var= Mc.INSTANCE;
        if (class815Var.isWorldLoaded() && (!class815Var.getPlayer().isUsingItem() || !class815Var.getPlayer().getActiveHand().equals(hand))) {
            class815Var.getInteractionManager().interactItem(class815Var.getPlayer(), hand);
            class815Var.getPlayer().usingItem = true;
        }
        this.useItem = true;
    }

    public void setUseItem(boolean z) {
        this.useItem = z;
    }

    public boolean isUseItem() {
        return this.useItem;
    }
}
