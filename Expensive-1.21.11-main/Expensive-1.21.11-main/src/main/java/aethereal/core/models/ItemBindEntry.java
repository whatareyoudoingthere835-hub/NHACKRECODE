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

import net.minecraft.item.ItemStack;

public class ItemBindEntry {
    public int count;
    public boolean active;
    public String keyText = "";
    public ItemStack stack = ItemStack.EMPTY;
    public final AnimatedFloat animation = new AnimatedFloat(250, Easings.LINEAR);

    public ItemBindEntry() {
    }

    public void animate(WeightedEngine class141Var) {
        this.animation.destination(this.active ? 1.0f : 0.0f);
        this.animation.animate(class141Var);
    }
}
